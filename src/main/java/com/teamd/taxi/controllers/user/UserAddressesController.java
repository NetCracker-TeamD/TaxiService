package com.teamd.taxi.controllers.user;

import com.google.gson.*;
import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserAddress;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import com.teamd.taxi.exception.PropertyNotFoundException;
import com.teamd.taxi.models.AddressForm;
import com.teamd.taxi.models.MapResponse;
import com.teamd.taxi.service.CustomerUserService;
import com.teamd.taxi.service.MapService;
import com.teamd.taxi.service.UserAddressService;
import com.teamd.taxi.validation.AddressExistenceValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserAddressesController {

    @Autowired
    private CustomerUserService userService;

    @Autowired
    private UserAddressService addressService;

    @Autowired
    private AddressExistenceValidator addressExistenceValidator;

    @Autowired
    private MapService mapService;

    @Resource
    private Environment env;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(addressExistenceValidator);
    }

    private static final Logger logger = Logger.getLogger(UserAddressesController.class);

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(UserAddress.class, new AddressSerializer())
            .create();

    @RequestMapping(value = "/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserAddresses() {
        AuthenticatedUser user = Utils.getCurrentUser();
        List<UserAddress> addressList = addressService.findAddressesByUserId(user.getId());
        return gson.toJson(addressList);
    }

    @RequestMapping(value = "/addAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object addAddress(@Valid AddressForm form, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("status", "validationError");
            response.put("fieldErrors", Utils.convertToMap(env, result));
            return response;
        }
        AuthenticatedUser auth = Utils.getCurrentUser();
        User user = userService.findById(auth.getId());
        UserAddress address = new UserAddress(null, form.getName(), form.getAddress());
        address.setUser(user);
        address = addressService.save(address);

        response.put("addressId", address.getId());
        response.put("status", "OK");
        return response;
    }

    @RequestMapping(value = "/deleteAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("#address.user.id == principal.id")
    public Map<String, Object> deleteAddress(@RequestParam("id") UserAddress address) {
        //TODO: think about whether its necessary or not
        if (address != null) {
            addressService.delete(address);
        }
        return new MapResponse()
                .put("status", "OK");
    }


    @RequestMapping(value = "/updateAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("#addressToUpdate.user.id == principal.id")
    public Map<String, Object> updateAddress(
            @RequestParam("id") UserAddress addressToUpdate,
            @Valid AddressForm form, BindingResult result) {
        if (result.hasErrors()) {
            return new MapResponse()
                    .put("status", "validationError")
                    .put("fieldErrors", Utils.convertToMap(env, result));
        }
        addressToUpdate.setAddress(form.getAddress());
        addressToUpdate.setName(form.getName());
        addressService.save(addressToUpdate);
        return new MapResponse()
                .put("status", "OK");
    }


    private void deleteAddress() {

    }

    private void updateAddress() {

    }

    private void saveNewAddress() {

    }

    @RequestMapping("/saveAddresses")
    @ResponseBody
    public Map<String, Object> save(Reader reader) throws PropertyNotFoundException, NotFoundException, MapServiceNotAvailableException {
        JsonArray addresses = (JsonArray) new JsonParser().parse(reader);
        AuthenticatedUser authenticatedUser = Utils.getCurrentUser();
        User user = userService.findById(authenticatedUser.getId());

        List<UserAddress> toSave = new ArrayList<>();
        List<UserAddress> toDelete = new ArrayList<>();

        for (Iterator<JsonElement> iterator = addresses.iterator(); iterator.hasNext(); ) {
            JsonObject addressRequest = (JsonObject) iterator.next();
            String action = getAndCheck(addressRequest, "action").getAsString();
            if ("update".equals(action) || "add".equals(action)) {
                String name = getAndCheck(addressRequest, "name").getAsString();
                String address = getAndCheck(addressRequest, "address").getAsString();
                if ("add".equals(action)) {
                    UserAddress newAddress = new UserAddress(null, name, address);
                    newAddress.setUser(user);
                    toSave.add(newAddress);
                } else {
                    int id = getAndCheck(addressRequest, "id").getAsInt();
                    UserAddress updatingAddress = addressService.findById(id);
                    if (updatingAddress.getUser().getId() != user.getId()) {
                        throw new AccessDeniedException("action[" + action + "] not allowed");
                    }
                    updatingAddress.setName(name);
                    updatingAddress.setAddress(address);
                    toSave.add(updatingAddress);
                }
            } else if ("remove".equals(action)) {
                int id = getAndCheck(addressRequest, "id").getAsInt();
                UserAddress updatingAddress = addressService.findById(id);
                if (updatingAddress.getUser().getId() != user.getId()) {
                    throw new AccessDeniedException("action[" + action + "] not allowed");
                }
            }
        }
        //проверка валидности адресов
        for (UserAddress address : toSave) {
            mapService.checkAddress(address.getAddress());
        }
        addressService.deleteAllByOneUser(user.getId(), toDelete);
        addressService.saveAll(toSave);
        return new MapResponse()
                .put("status", "ok");
    }


    private JsonElement getAndCheck(JsonObject object, String property) throws PropertyNotFoundException {
        JsonElement element = object.get(property);
        if (element == null) {
            throw new PropertyNotFoundException(property);
        }
        return element;
    }

    private static class AddressSerializer implements JsonSerializer<UserAddress> {

        @Override
        public JsonElement serialize(UserAddress address, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject f = new JsonObject();
            f.addProperty("id", address.getId());
            f.addProperty("name", address.getName());
            f.addProperty("address", address.getAddress());
            return f;
        }
    }

    @ExceptionHandler({
            PropertyNotFoundException.class
    })
    @ResponseBody
    public Map<String, Object> handleJsontExceptions(Exception e) {
        return new MapResponse()
                .put("exception", e.getClass().getName())
                .put("message", e.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    @ResponseBody
    public Map<String, Object> handleIllegalArgumentException() {
        return new MapResponse().put("status", "notFound");
    }
}
