package com.teamd.taxi.controllers;

import com.google.gson.*;
import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.authentication.AuthenticatedUser;
import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.*;
import com.teamd.taxi.models.*;

import static com.teamd.taxi.entity.RouteStatus.*;

import com.teamd.taxi.service.*;
import com.teamd.taxi.service.email.MailService;
import com.teamd.taxi.service.email.Notification;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.persistence.criteria.Order;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.teamd.taxi.controllers.user.UserAddressesController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class OrderController {

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private CarClassService carClassService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Autowired
    private CustomerUserService userService;

    @Autowired
    private FeatureService featureService;

    @Autowired
    private UserAddressService addressService;

    @Autowired
    private PriceCountService priceCountService;

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private UserAddressesController userAddressesController;

    @Autowired
    private MailService mailService;

    private static final Logger logger = Logger.getLogger(OrderController.class);

    @Autowired
    @Qualifier("dateFormatter")
    private SimpleDateFormat dateFormat;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServiceType.class, new ServiceTypeSerializer())
            .registerTypeAdapter(CarClass.class, new CarClassSerializer())
            .registerTypeAdapter(Feature.class, new FeatureSerializer())
            .registerTypeAdapter(UserAddress.class, new AddressSerializer())
            .create();


    private void addUserAddressesToModel(Model model) {
        if (Utils.isAuthenticated()) {
            try {
                model.addAttribute("addressesJSON", userAddressesController.getUserAddresses());
            } catch (IOException e) {
                model.addAttribute("addressesJSON", "[]");
            }
        } else {
            model.addAttribute("addressesJSON", "[]");
        }
    }

    @RequestMapping("/order")
    public String order(Model model) {
        AbstractAuthenticationToken auth = (AbstractAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        logger.info("Auth status: " + auth.getPrincipal()
                + ", " + auth.getCredentials() + ", " + auth.getAuthorities() + ", " + auth.isAuthenticated());
        model.addAttribute("servicesJSON", getServices());
        addUserAddressesToModel(model);
        return "user/order";
    }

    @RequestMapping(value = "/order/{id}/{secretKey}", produces = "application/json;charset=UTF-8")
    public String order(
            @PathVariable(value = "id") long orderId,
            @PathVariable(value = "secretKey") String secretKey,
            Model model) {
        try {
            String orderInfo = getOrder(orderId, secretKey);
            model.addAttribute("orderInfoJSON", orderInfo);
            model.addAttribute("servicesJSON", getServices());
            addUserAddressesToModel(model);

            return "/user/order";
        } catch (ItemNotFoundException e) {
            return "redirect:/about";
        } catch (SecretKeyMismatchException e) {
            //add error handling
            return "redirect:/about";
        }
    }

    @RequestMapping(value = "/order/{id}", produces = "application/json;charset=UTF-8")
    public String order(
            @PathVariable(value = "id") long orderId,
            Model model) {
        return order(orderId, null, model);
    }

    @RequestMapping(value = "/services", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getServices() {
        logger.info("request for services information");
        HashMap<String, Object> retVal = new HashMap<>();

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        boolean userAuthenticated = !(authentication instanceof AnonymousAuthenticationToken);

        if (userAuthenticated) {
            long userId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
            long refusedOrders = blackListService.countByUserId(userId);
            if (refusedOrders >= BlackListService.REFUSED_ORDERS_LIMIT) {
                retVal.put("status", "blockedDueToRefuses");
                return gson.toJson(retVal);
            }
            retVal.put("locations", addressService.findAddressesByUserId(userId));
        }
        retVal.put("status", "OK");
        retVal.put("userAuthenticated", userAuthenticated);
        retVal.put("services", serviceTypeService.findAll());

        return gson.toJson(retVal);
    }

    //Set of auxiliary methods to work with JSON
    private JsonElement getAndCheck(JsonObject object, String propName) throws PropertyNotFoundException {
        JsonElement element = object.get(propName);
        if (element == null) {
            throw new PropertyNotFoundException(propName + " is null");
        }
        return element;
    }

    private List<String> readStringList(JsonObject jsonObject, String propName, boolean checkEmpty) throws PropertyNotFoundException {
        List<String> list = getStrings(getAndCheck(jsonObject, propName));
        if (list == null) {
            throw new PropertyNotFoundException(propName + " has incorrect type");
        }
        if (checkEmpty && list.isEmpty()) {
            throw new PropertyNotFoundException(propName + " is empty");
        }
        return list;
    }

    private List<Integer> readIntList(JsonObject jsonObject, String propName, boolean checkEmpty) throws PropertyNotFoundException {
        List<Integer> list = getInts(getAndCheck(jsonObject, propName));
        if (list == null) {
            throw new PropertyNotFoundException(propName + " has incorrect type");
        }
        if (checkEmpty && list.isEmpty()) {
            throw new PropertyNotFoundException(propName + " is empty");
        }
        return list;
    }

    private List<String> getStrings(JsonElement jsonElement) {
        List<String> list = null;
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            list = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(jsonArray.get(i).getAsString());
            }
        } else if (jsonElement.isJsonPrimitive()) {
            list = new ArrayList<>();
            list.add(jsonElement.getAsJsonPrimitive().getAsString());
        }
        return list;
    }

    private List<Integer> getInts(JsonElement intsElement) {
        List<Integer> list = null;
        if (intsElement.isJsonArray()) {
            JsonArray intArray = intsElement.getAsJsonArray();
            list = new ArrayList<>(intArray.size());
            for (int i = 0; i < intArray.size(); i++) {
                list.add(intArray.get(i).getAsInt());
            }
            return list;
        } else if (intsElement.isJsonPrimitive()) {
            list = new ArrayList<>();
            list.add(intsElement.getAsJsonPrimitive().getAsInt());
        }
        return list;
    }

    //05/13/2015 12:47 AM
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    static {
        formatter.setLenient(false);
    }

    private Calendar parseDate(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatter.parse(date));
        return calendar;
    }

    private TaxiOrderForm fillForm(JsonObject orderObject) throws PropertyNotFoundException,
            ItemNotFoundException, ParseException {
        //тип сервиса
        JsonPrimitive serviceId = (JsonPrimitive) getAndCheck(orderObject, "serviceType");
        ServiceType serviceType = serviceTypeService.findById(serviceId.getAsInt());
        if (serviceType == null) {
            throw new ItemNotFoundException();
        }
        TaxiOrderForm form = new TaxiOrderForm();
        //тип сервиса
        form.setServiceType(serviceType);
        //точки отправления
        form.setSource(readStringList(orderObject, "start_addresses", true));
        //промежуточные точки
        Boolean isChain = serviceType.isDestinationLocationsChain();
        if (isChain != null && isChain) {
            JsonElement intermediateElement = orderObject.get("intermediate_addresses");
            if (intermediateElement != null) {
                form.setIntermediate(getStrings(intermediateElement));
            }
        }
        //точки назначения
        if (serviceType.isDestinationRequired()) {
            form.setDestination(readStringList(orderObject, "destination_addresses", true));
        }
        //количество автомобилей
        JsonElement carsAmountElement = orderObject.get("cars_amount");
        if (carsAmountElement != null) {
            form.setCarsAmount(getInts(carsAmountElement));
        } else {
            form.setCarsAmount(Arrays.asList(1));
        }
        //пол водителя
        JsonPrimitive driverSexPrimitive = (JsonPrimitive) getAndCheck(orderObject, "driver_sex");
        form.setDriverSex(driverSexPrimitive.getAsString().toUpperCase());
        //тип оплаты
        form.setPaymentType(PaymentType.valueOf(
                ((JsonPrimitive) getAndCheck(orderObject, "payment_type"))
                        .getAsString().toUpperCase()
        ));
        //класс автомобиля
        JsonPrimitive carClassPrimitive = orderObject.getAsJsonPrimitive("car_class");
        if (carClassPrimitive != null) {
            int carClassId = carClassPrimitive.getAsInt();
            CarClass carClass = carClassService.findById(carClassId);
            if (carClass == null) {
                throw new ItemNotFoundException("carClass: " + carClassId);
            }
            form.setCarClass(carClass);
        }
        //фичи
        JsonElement featuresElement = orderObject.get("features");
        if (featuresElement != null) {
            List<Integer> featureIds = getInts(featuresElement);
            List<Feature> features = featureService.findByIdList(featureIds);
            if (featureIds.size() != features.size()) {
                throw new ItemNotFoundException("some features not found");
            }
            form.setFeatures(features);
        }
        //время заказа
        boolean now = serviceType.isTimingNow();
        boolean specified = serviceType.isTimingSpecified();
        JsonPrimitive nowPrimitive = orderObject.getAsJsonPrimitive("time");
        JsonPrimitive specifiedPrimitive = orderObject.getAsJsonPrimitive("time_specified");
        if (now && nowPrimitive != null) {
            form.setExecDate(Calendar.getInstance());
        } else if (specified && specifiedPrimitive != null) {
            Calendar calendar = parseDate(specifiedPrimitive.getAsString());
            if (calendar.compareTo(Calendar.getInstance()) < 0) {
                throw new IllegalArgumentException("date from the past received: " + calendar.getTime());
            }
            form.setExecDate(calendar);
        } else {
            throw new PropertyNotFoundException("timing");
        }
        return form;
    }


    @RequestMapping(value = "/countPrice", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Price countApproximatePrice(Reader reader)
            throws ParseException, PropertyNotFoundException, ItemNotFoundException, MapServiceNotAvailableException, NotFoundException, NotCompatibleException {
        JsonObject orderObject = (JsonObject) new JsonParser().parse(reader);
        TaxiOrderForm form = fillForm(orderObject);
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        Long userId = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            userId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        }
        TaxiOrder order = taxiOrderService.fillOrder(form, null);
        return priceCountService.approximateOrderPrice(order, userId);
    }

    @RequestMapping(value = "/makeOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> makeOrder(Reader reader) throws IOException,
            PropertyNotFoundException, ItemNotFoundException, ParseException,
            NotCompatibleException, NotFoundException, MapServiceNotAvailableException, MessagingException {
        //заполняем форму
        JsonObject orderObject = (JsonObject) new JsonParser().parse(reader);
        logger.info("Received orderObject: " + orderObject);
        TaxiOrderForm form = fillForm(orderObject);
        logger.info("Resulting TO form: " + form);
        //находим или создаем пользователя
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        User user;
        if (authentication instanceof AnonymousAuthenticationToken) {
            JsonElement name = getAndCheck(orderObject, "firstName");
            JsonElement lastName = getAndCheck(orderObject, "lastName");
            JsonElement emailElement = orderObject.get("email");
            JsonElement phoneNumber = getAndCheck(orderObject, "phoneNumber");
            user = new User(null, name.getAsString(), lastName.getAsString(), UserRole.ROLE_ANONYMOUS, phoneNumber.getAsString());
            if (emailElement != null) {
                String email = emailElement.getAsString();
                if (!userService.isEmailFree(email)) {
                    return new MapResponse().put("status", "emailNotFree");
                }
                user.setEmail(email);
            }
            user = userService.save(user);
        } else {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            user = userService.findById(authenticatedUser.getId());
        }
        //вносим заказ в базу
        TaxiOrder order;
        try {
            order = taxiOrderService.createNewTaxiOrder(form, user);
        } catch (OrderingBlockedDueRefusedException exception) {
            //превышено количество refused-заказов, отказываемся от обслуживания
            return new MapResponse().put("status", "blockedDueToRefuses");
        }
        //создаем ссылку на заказ
        String link;
        if (user.getUserRole() == UserRole.ROLE_CUSTOMER) {
            link = MvcUriComponentsBuilder.fromMethodName(OrderController.class, "order", order.getId(), null)
                    .toUriString();
        } else {
            link = MvcUriComponentsBuilder.fromMethodName(OrderController.class, "order", order.getId(), order.getSecretViewKey(), null)
                    .toUriString();
        }
        System.out.println("link = " + link);
        String userEmail = user.getEmail();
        if (userEmail != null) {
            mailService.sendNotification(userEmail, Notification.NEW_ORDER, new Object[]{order.getId(), link});
        }
        //отправка ответа
        return new MapResponse().put("status", "OK")
                .put("trackLink", link);
    }

    private void checkAccess(TaxiOrder order, String secretKey, boolean allowAdmin) throws SecretKeyMismatchException {
        //TODO: убрать это
        if (true) {
            return;
        }
        User customer = order.getCustomer();
        AccessDeniedException accessDeniedException = new AccessDeniedException("not enough rights on[" + order.getId() + "]");
        //заказчик - зарегистрированный пользовательы
        if (customer.getUserRole() == UserRole.ROLE_CUSTOMER) {
            //пользователь не авторизован
            if (Utils.isAuthenticated()) {
                throw accessDeniedException;
            }
            String userRole = Utils.getCurrentUserRole();
            //текущий пользователь администратор
            if (userRole.equals("ROLE_ADMINISTRATOR")) {
                //действие разрешено для администратора
                if (allowAdmin) {
                    return;
                }
                //иначе
                throw accessDeniedException;
            }
            //если текущий пользователь авторизован и его id совпадает с id заказчика данного заказа
            if (userRole.equals("ROLE_CUSTOMER") && customer.getId().equals(Utils.getCurrentUser().getId())) {
                return;
            }
            //все остальное
            throw accessDeniedException;
        } //заказчик - анонимный пользователь
        else if (!order.getSecretViewKey().equals(secretKey)) {
            throw new SecretKeyMismatchException();
        }
    }

    @RequestMapping(value = "/setUpdating", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> setUpdating(
            @RequestParam("id") TaxiOrder order,
            @RequestParam(value = "secretKey", required = false) String secretKey)
            throws SecretKeyMismatchException {
        if (order == null) {
            return new MapResponse().put("status", "notFound");
        }
        checkAccess(order, secretKey, false);
        String status = "OK";
        try {
            taxiOrderService.setUpdating(order.getId());
        } catch (OrderUpdatingException e) {
            status = "orderPickedUp";
        }
        return new MapResponse().put("status", status);
    }

    @RequestMapping(value = "/cancelUpdating", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> cancelUpdating(
            @RequestParam("id") TaxiOrder order,
            @RequestParam(value = "secretKey", required = false) String secretKey)
            throws OrderUpdatingException,
            SecretKeyMismatchException {
        if (order == null) {
            return new MapResponse().put("status", "notFound");
        }
        checkAccess(order, secretKey, false);
        taxiOrderService.cancelUpdating(order.getId());
        return new MapResponse().put("status", "OK");
    }

    @RequestMapping(value = "/cancelOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> cancelOrder(
            @RequestParam("id") TaxiOrder order,
            @RequestParam(value = "secretKey", required = false) String secretKey)
            throws OrderUpdatingException, SecretKeyMismatchException {
        if (order == null) {
            return new MapResponse().put("status", "notFound");
        }
        checkAccess(order, secretKey, false);
        String status = "OK";
        try {
            taxiOrderService.cancelOrder(order.getId());
        } catch (OrderUpdatingException e) {
            status = "orderPickedUp";
        }
        return new MapResponse().put("status", status);
    }

    @RequestMapping(value = "/updateOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> updateOrder(Reader reader)
            throws ParseException, PropertyNotFoundException, ItemNotFoundException,
            MapServiceNotAvailableException, NotFoundException, NotCompatibleException,
            OrderUpdatingException, SecretKeyMismatchException {
        JsonObject orderObject = (JsonObject) new JsonParser().parse(reader);
        //ключ, необх. для обновления заказа анон. пользователем
        String secretKey = null;
        JsonElement keyElement = orderObject.get("secretKey");
        if (keyElement != null) {
            secretKey = keyElement.getAsString();
        }
        //находим сам обьект
        long orderId = getAndCheck(orderObject, "orderId").getAsLong();
        TaxiOrder order = taxiOrderService.findOneById(orderId);
        if (order == null) {
            return new MapResponse().put("status", "notFound");
        }
        //проверяем права
        checkAccess(order, secretKey, false);
        TaxiOrderForm form = fillForm(orderObject);
        taxiOrderService.updateTaxiOrder(orderId, form);
        return new MapResponse().put("status", "OK");
    }

    @RequestMapping(value = "/getOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getOrder(
            @RequestParam("id") long orderId,
            @RequestParam(value = "secretKey", required = false) String secretKey
    ) throws ItemNotFoundException, SecretKeyMismatchException {
        TaxiOrder order = taxiOrderService.findOneById(orderId);
        if (order == null) {
            throw new ItemNotFoundException("order [" + orderId + "] not found");
        }
        checkAccess(order, secretKey, true);
        return gson.toJson(convertTaxiOrderToObject(order));
    }

    @ExceptionHandler(SecretKeyMismatchException.class)
    @ResponseBody
    public Map<String, Object> secretKeyMismatchHandler(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        return new MapResponse().put("status", "secretKeyMismatch");
    }

    @ExceptionHandler({
            ClassCastException.class,
            PropertyNotFoundException.class,
            NumberFormatException.class,
            ItemNotFoundException.class,
            ParseException.class,
            NotCompatibleException.class,
            NotFoundException.class,
            MapServiceNotAvailableException.class,
            JsonSyntaxException.class,
            JsonParseException.class,
            IllegalArgumentException.class,
            OrderUpdatingException.class
    })
    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        logger.error(e);
        response.setContentType("application/json");
        response.getWriter().append("{" +
                "\"success\": false," +
                "\"error\": \"" + e.getClass() + "\", " +
                "\"message\":\"" + e.getMessage() + "\"" +
                "}");
    }

    private JsonObject convertToObject(Route route) {
        JsonObject routeObject = new JsonObject();
        routeObject.addProperty("status", route.getStatus().name());
        Calendar startTime = route.getStartTime();
        Calendar completeTime = route.getCompletionTime();
        if (startTime != null) {
            routeObject.addProperty("startTime", formatter.format(startTime.getTime()));
            if (completeTime != null) {
                routeObject.addProperty("completeTime", formatter.format(completeTime.getTime()));
            }
        }
        return routeObject;
    }

    private List<List<Route>> separateRoutes(List<AssembledRoute> assembledRoutes) {
        int chainsAmount = assembledRoutes.get(0).getRoutes().size();
        List<List<Route>> chains = new ArrayList<>(chainsAmount);
        for (int i = 0; i < chainsAmount; i++) {
            List<Route> chain = new ArrayList<>();
            for (AssembledRoute assembledRoute : assembledRoutes) {
                chain.add(assembledRoute.getRoutes().get(i));
            }
            chains.add(chain);
        }
        return chains;
    }

    private static final List<RouteStatus> HAS_START_TIME = Arrays.asList(IN_PROGRESS, COMPLETED);

    private JsonObject convertChainToObject(List<Route> chain) {
        JsonObject chainObject = new JsonObject();
        Route first = chain.get(0);
        Route last = chain.get(chain.size() - 1);
        //status
        RouteStatus firstStatus = first.getStatus();
        RouteStatus result = firstStatus;
        RouteStatus lastStatus = last.getStatus();
        if (result == COMPLETED && lastStatus != COMPLETED) {
            result = lastStatus;
        }
        chainObject.addProperty("status", result.name());
        //startTime
        if (HAS_START_TIME.contains(firstStatus)) {
            Calendar startTime = first.getStartTime();
            Calendar completeTime = null;
            chainObject.addProperty("startTime", formatter.format(startTime.getTime()));
            if (lastStatus == COMPLETED) {
                completeTime = last.getCompletionTime();
            } else if (lastStatus == REFUSED) {
                //если попали сюда, значит в цепочке как минимум 2 маршрута
                int i = chain.size() - 2;
                while (chain.get(i).getStatus() == REFUSED) {
                    i--;
                }
                System.out.println(chain);
                System.out.println(firstStatus);
                System.out.println(lastStatus);
                System.out.println(i);
                System.out.println(chain.get(i));
                System.out.println(chain.get(i).getCompletionTime().getTime());
                completeTime = chain.get(i).getCompletionTime();
            }
            if (completeTime != null) {
                chainObject.addProperty("completeTime", formatter.format(completeTime.getTime()));
            }
        }
        return chainObject;
    }

    private JsonObject convertTaxiOrderToObject(TaxiOrder order) {
        //атрибуты всего заказа
        JsonObject orderObject = new JsonObject();
        //состояние (обновляется или нет)
        boolean updating = false;
        for (Route route : order.getRoutes()) {
            if (route.getStatus() == UPDATING) {
                updating = true;
                break;
            }
        }
        orderObject.addProperty("orderId", order.getId());
        orderObject.addProperty("updating", updating);
        orderObject.addProperty("executionDate", formatter.format(order.getExecutionDate().getTime()));
        orderObject.addProperty("registrationDate", formatter.format(order.getRegistrationDate().getTime()));
        orderObject.addProperty("musicStyle", order.getMusicStyle());
        orderObject.addProperty("comment", order.getComment());
        orderObject.addProperty("carClassId", order.getCarClass().getId());
        ServiceType serviceType = order.getServiceType();
        orderObject.addProperty("serviceType", serviceType.getId());
        Sex driverSex = order.getDriverSex();
        if (driverSex != null) {
            orderObject.addProperty("driverSex", driverSex.name());
        }
        orderObject.addProperty("paymentType", order.getPaymentType().name());
        JsonArray features = new JsonArray();
        for (Feature feature : order.getFeatures()) {
            features.add(new JsonPrimitive(feature.getId()));
        }
        orderObject.add("features", features);
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(order);
        //марштуры
        JsonArray startAddresses = new JsonArray();
        JsonArray intermediateAddresses = new JsonArray();
        JsonArray destinationAddresses = new JsonArray();
        JsonArray carsAmount = new JsonArray();

        List<AssembledRoute> assembledRoutes = assembledOrder.getAssembledRoutes();
        System.out.println(assembledRoutes);
        Boolean isChain = serviceType.isDestinationLocationsChain();
        if (isChain != null && isChain) {
            startAddresses.add(new JsonPrimitive(assembledRoutes.get(0).getSource()));
            int i = 1;
            for (; i < assembledRoutes.size(); i++) {
                intermediateAddresses.add(new JsonPrimitive(assembledRoutes.get(i).getSource()));
            }
            destinationAddresses.add(new JsonPrimitive(assembledRoutes.get(--i).getDestination()));
            List<List<Route>> chains = separateRoutes(assembledRoutes);
            JsonArray routes = new JsonArray();
            for (List<Route> chain : chains) {
                routes.add(convertChainToObject(chain));
            }
            carsAmount.add(routes);
        } else if (serviceType.isMultipleSourceLocations()) {
            for (AssembledRoute assembledRoute : assembledRoutes) {
                startAddresses.add(new JsonPrimitive(assembledRoute.getSource()));
                JsonArray routes = new JsonArray();
                for (Route route : assembledRoute.getRoutes()) {
                    routes.add(convertToObject(route));
                }
                carsAmount.add(routes);
            }
            destinationAddresses.add(new JsonPrimitive(assembledRoutes.get(0).getDestination()));
        } else {
            AssembledRoute singleRoute = assembledRoutes.get(0);
            startAddresses.add(new JsonPrimitive(singleRoute.getSource()));
            JsonArray routes = new JsonArray();
            for (Route route : singleRoute.getRoutes()) {
                routes.add(convertToObject(route));
            }
            carsAmount.add(routes);
        }
        orderObject.add("startAddresses", startAddresses);
        orderObject.add("intermediateAddresses", intermediateAddresses);
        orderObject.add("destinationAddresses", destinationAddresses);
        /*
        * cars: [
        *   [
        *       {
        *           status: 'QUEUED',
        *           startTime:
        *           completeTime:
        *       },
        *       ...
        *   ],
        *   ...
        * ]
        * */
        orderObject.add("carsAmount", carsAmount);
        return orderObject;
    }

    private static class ServiceTypeSerializer implements JsonSerializer<ServiceType> {

        @Override
        public JsonElement serialize(ServiceType serviceType, Type type, JsonSerializationContext context) {
            JsonObject st = new JsonObject();
            st.addProperty("serviceId", serviceType.getId());
            st.addProperty("name", serviceType.getName());
            st.addProperty("multipleSourceLocations", serviceType.isMultipleSourceLocations());
            st.addProperty("multipleDestinationLocations", serviceType.isMultipleDestinationLocations());
            st.addProperty("chain", serviceType.isDestinationLocationsChain());
            st.addProperty("destinationRequired", serviceType.isDestinationRequired());
            st.addProperty("specifyCarsNumbers", serviceType.isSpecifyCarNumbers());
            st.addProperty("minCarsNumbers", serviceType.getMinCarNumber());
            st.add("allowedFeatures", context.serialize(serviceType.getAllowedFeatures()));
            st.add("allowedCarClasses", context.serialize(serviceType.getAllowedCarClasses()));
            List<String> timing = new ArrayList<>();
            if (serviceType.isTimingNow()) {
                timing.add("now");
            }
            if (serviceType.isTimingSpecified()) {
                timing.add("specified");
            }
            st.add("timing", context.serialize(timing));
            return st;
        }
    }

    private static class CarClassSerializer implements JsonSerializer<CarClass> {

        @Override
        public JsonElement serialize(CarClass carClass, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject cc = new JsonObject();
            cc.addProperty("classId", carClass.getId());
            cc.addProperty("className", carClass.getClassName());
            return cc;
        }
    }

    private static class FeatureSerializer implements JsonSerializer<Feature> {

        @Override
        public JsonElement serialize(Feature feature, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject f = new JsonObject();
            f.addProperty("featureId", feature.getId());
            f.addProperty("featureName", feature.getName());
            return f;
        }
    }

    private static class AddressSerializer implements JsonSerializer<UserAddress> {

        @Override
        public JsonElement serialize(UserAddress address, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject f = new JsonObject();
            f.addProperty("name", address.getName());
            f.addProperty("address", address.getAddress());
            return f;
        }
    }
}
