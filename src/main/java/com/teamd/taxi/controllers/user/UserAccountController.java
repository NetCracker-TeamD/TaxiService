package com.teamd.taxi.controllers.user;

import com.google.gson.*;
import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.models.MapResponse;
import com.teamd.taxi.models.UserAccountForm;
import com.teamd.taxi.service.CustomerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Олег on 20.05.2015.
 */
@Controller
@RequestMapping("/user")
public class UserAccountController {

    @Autowired
    private CustomerUserService userService;

    @Autowired
    private UserAddressesController userAddressesController;

    @Resource
    private Environment env;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(User.class, new UserSerializer())
            .create();
    @RequestMapping("/account")
    public String account(Model model){
        if (!Utils.isAuthenticated()) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("addressesJSON", userAddressesController.getUserAddresses());
        } catch (IOException e) {
            model.addAttribute("addressesJSON", "[]");
        }
        return "/user/account";
    }

    @RequestMapping(value = "/getAccountData", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAccountData() {
        User user = userService.findById(Utils.getCurrentUser().getId());
        return gson.toJson(user);
    }

    @RequestMapping(value = "/updateAccount", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> updateAccountData(@Valid UserAccountForm accountForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new MapResponse()
                    .put("errors", Utils.convertToMap(env, bindingResult));
        }
        User currentUser = userService.findById(Utils.getCurrentUser().getId());
        currentUser.setFirstName(accountForm.getFirstName());
        currentUser.setLastName(accountForm.getLastName());
        currentUser.setEmail(accountForm.getEmail());
        currentUser.setPhoneNumber(accountForm.getPhoneNumber());

        userService.save(currentUser);

        return new MapResponse().put("status", "ok");
    }

    private static class UserSerializer implements JsonSerializer<User> {
        @Override
        public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject userObject = new JsonObject();
            userObject.addProperty("firstName", user.getFirstName());
            userObject.addProperty("lastName", user.getLastName());
            userObject.addProperty("phoneNumber", user.getPhoneNumber());
            userObject.addProperty("email", user.getEmail());
            return userObject;
        }
    }
}
