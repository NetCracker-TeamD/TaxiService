package com.teamd.taxi.controllers;

import com.google.gson.*;
import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.service.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private ServiceTypeService serviceTypeService;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServiceType.class, new ServiceTypeSerializer())
            .registerTypeAdapter(CarClass.class, new CarClassSerializer())
            .registerTypeAdapter(Feature.class, new FeatureSerializer())
            .create();

    @RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getServices() {
        HashMap<String, Object> retVal = new HashMap<>();
        retVal.put("status", "OK");
        retVal.put("userAuthenticated",
                !(SecurityContextHolder
                        .getContext()
                        .getAuthentication() instanceof AnonymousAuthenticationToken)
        );
        retVal.put("services", serviceTypeService.findAll());
        return gson.toJson(retVal);
    }

    @RequestMapping("/order")
    public String sendOrderFormPage() {
        return "order-form";
    }

    @RequestMapping("/makeOrder")
    @ResponseBody
    public Map<String, Object> makeOrder(@RequestParam MultiValueMap<String, String> params) {
        return null;
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
}
