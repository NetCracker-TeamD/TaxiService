package com.teamd.taxi.controllers.user;

import com.google.gson.*;
import com.teamd.taxi.authentication.Utils;
import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.models.*;
import com.teamd.taxi.service.PagingLinksGenerator;
import com.teamd.taxi.service.TaxiOrderService;
import com.teamd.taxi.service.TaxiOrderSpecificationFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specifications.*;

import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.domain.Sort.Order;


@Controller
@RequestMapping("/user")
public class UserHistoryController {

    @Autowired
    private TaxiOrderService orderService;

    @Autowired
    private PagingLinksGenerator linksGenerator;

    @Autowired
    private TaxiOrderSpecificationFactory factory;

    private static final int PAGE_SIZE = 20;

    @Autowired
    @Qualifier("dateFormatter")
    private SimpleDateFormat dateFormat;

    private static final Logger logger = Logger.getLogger(UserHistoryController.class);

    private Gson gson;

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(TaxiOrder.class, new TaxiOrderSerializer(dateFormat))
                    .registerTypeAdapter(Feature.class, new FeatureSerializer())
                    .registerTypeAdapter(AssembledRoute.class, new AssembledRouteSerializer(dateFormat))
                    .registerTypeAdapter(ServiceType.class, new ServiceTypeSerializer())
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }
        return gson;
    }

    private Map<String, String> allowedSortProperties;

    private Map<String, String> allowedSortProperties() {
        if (allowedSortProperties == null) {
            allowedSortProperties = new TreeMap<>();
            allowedSortProperties.put("registrationDate", "Registration Date");
            allowedSortProperties.put("serviceType.name", "Service");
            allowedSortProperties.put("paymentType", "Payment Type");
            allowedSortProperties.put("driverSex", "Driver Sex");
        }
        return allowedSortProperties;
    }

    private List<String> allowedAdditionalProperties;

    private List<String> allowedAdditionalProperties() {
        if (allowedAdditionalProperties == null) {
            allowedAdditionalProperties = Arrays.asList("from", "to");
        }
        return allowedAdditionalProperties;
    }

    private void addSortingParams(UriComponentsBuilder builder, MultiValueMap<String, String> params) {
        List<String> sortingParams = params.get("sort");
        if (sortingParams != null) {
            for (String sortingParam : sortingParams) {
                builder.queryParam("sort", sortingParam);
            }
        }
    }

    private void addAdditionalParams(UriComponentsBuilder builder, MultiValueMap<String, String> params) {
        for (String paramName : allowedAdditionalProperties()) {
            List<String> values = params.get(paramName);
            if (values != null && values.size() > 0) {
                builder.queryParam(paramName, values.get(0));
            }
        }
    }

    private static Calendar getCalendarByTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    private Specification<TaxiOrder> resolveSpecification(long userId, MultiValueMap<String, String> params) {
        List<Specification<TaxiOrder>> specs = new ArrayList<>();
        specs.add(factory.userIdEqual(userId));
        //"to" date
        List<String> toVals = params.get("to");
        if (toVals != null && toVals.size() > 0) {
            try {
                long toDate = Long.parseLong(toVals.get(0));
                specs.add(factory.registrationDateLessThan(getCalendarByTime(toDate)));
            } catch (NumberFormatException exception) {
            }
        }
        //"from" date
        List<String> fromVals = params.get("from");
        if (fromVals != null && fromVals.size() > 0) {
            try {
                long fromDate = Long.parseLong(fromVals.get(0));
                specs.add(factory.registrationDateGreaterThan(getCalendarByTime(fromDate)));
            } catch (NumberFormatException exception) {
            }
        }
        System.out.println(specs.size());
        //join specifications
        Iterator<Specification<TaxiOrder>> specIt = specs.iterator();
        Specifications<TaxiOrder> spec = Specifications.where(specIt.next());
        while (specIt.hasNext()) {
            spec = spec.and(specIt.next());
        }
        return spec;
    }

    //filtering sorting parameters
    private Sort filterSort(Sort raw) {
        List<Order> filtered = new ArrayList<>();
        if (raw != null) {
            Map<String, String> allowed = allowedSortProperties();
            for (Iterator<Order> it = raw.iterator(); it.hasNext(); ) {
                Order next = it.next();
                if (allowed.containsKey(next.getProperty())) {
                    filtered.add(next);
                    break; //for this time, only one property allowed for sorting at the same time
                }
            }
        }
        //default sort order
        if (filtered.isEmpty()) {
            filtered.add(new Order(Sort.Direction.DESC, "registrationDate"));
        }
        return new Sort(filtered);
    }

    private List<String> getStringBySort(Sort sort) {
        if (sort == null) {
            return null;
        }
        List<String> sortList = new ArrayList<>();
        for (Iterator<Sort.Order> it = sort.iterator(); it.hasNext(); ) {
            Sort.Order order = it.next();
            sortList.add(order.getProperty() + "," + order.getDirection().name());
        }
        return sortList;
    }

    private List<String> extractPropertiesFromSort(Sort sort) {
        List<String> props = new ArrayList<>();
        for (Iterator<Order> it = sort.iterator(); it.hasNext(); ) {
            props.add(it.next().getProperty());
        }
        return props;
    }

    private Map<String, String> extractAdditionalParams(MultiValueMap<String, String> params) {
        Map<String, String> foundParams = new HashMap<>();
        for (String paramName : allowedAdditionalProperties()) {
            List<String> values = params.get(paramName);
            if (values != null && values.size() > 0) {
                foundParams.put(paramName, values.get(0));
            }
        }
        return foundParams;
    }


    private Map<String, Object> loadHistoryByUserId(
            long userId,
            UriComponentsBuilder builder,
            Pageable pageable,
            MultiValueMap<String, String> params
    ) {
        //retrieving data from database
        Sort filtered = filterSort(pageable.getSort());
        //TODO: добавить второстепенную сортировку по id (предсказуемость порядка появления результатов)
        Page<TaxiOrder> page = orderService.findAll(
                resolveSpecification(userId, params),
                new PageRequest(pageable.getPageNumber(), PAGE_SIZE, filtered)
        );
        List<TaxiOrder> content = page.getContent();
        if (content.size() != 0) {
            //assembling orders
            List<AssembledOrder> assembledOrders = new ArrayList<>(content.size());
            for (TaxiOrder taxiOrder : content) {
                assembledOrders.add(AssembledOrder.assembleOrder(taxiOrder));
            }
            //generating links for paging
            addSortingParams(builder, params);
            addAdditionalParams(builder, params);
            List<PagingLink> links = linksGenerator.generateLinks(page, builder);
            //send data to the receiver
            return new MapResponse()
                    .put("orders", assembledOrders)
                    .put("pageDetails", new PageDetails(page))
                    .put("links", links)
                    .put("status", "ok");
        } else {
            return new MapResponse()
                    .put("status", "notFound");
        }
    }

    @RequestMapping(value = "/loadHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or #userId == principal.id")
    public String loadHistory(Pageable pageable, @RequestParam("userId") long userId, @RequestParam MultiValueMap<String, String> params) {
        return getGson().toJson(
                loadHistoryByUserId(
                        userId,
                        MvcUriComponentsBuilder.fromMethodName(
                                UserHistoryController.class,
                                "loadHistory",
                                null, userId, null
                        ),
                        pageable,
                        params
                )
        );
    }


    private void populateModel(Pageable pageable, long userId, MultiValueMap<String, String> params, Model model) {
        Sort sort = filterSort(pageable.getSort());
        if (sort != null) {
            List<String> sorts = getStringBySort(sort);
            model.addAttribute("sorts", sorts);
        }
        model.addAttribute("sort", sort);
        model.addAttribute("pageable", pageable);
        model.addAttribute("allowedSortProperties", allowedSortProperties());
        model.addAttribute("selectedSorts", extractPropertiesFromSort(sort));
        Map<String, String> additionalParams = extractAdditionalParams(params);
        additionalParams.put("userId", userId + "");
        model.addAttribute("additionalParams", additionalParams);
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistoryCurrentUser(Pageable pageable, @RequestParam MultiValueMap<String, String> params, Model model) {
        populateModel(pageable, Utils.getCurrentUser().getId(), params, model);
        return "user/user-history";
    }

    @RequestMapping(value = "/history/{userId}", method = RequestMethod.GET)
    public String viewHistoryAnyUser(
            Pageable pageable,
            @PathVariable("userId") long userId,
            @RequestParam MultiValueMap<String, String> params,
            Model model) {
        populateModel(pageable, userId, params, model);
        return "user/user-history";
    }

    private static class TaxiOrderSerializer implements JsonSerializer<TaxiOrder> {

        private SimpleDateFormat fmt;

        public TaxiOrderSerializer(SimpleDateFormat fmt) {
            this.fmt = fmt;
        }

        @Override
        public JsonElement serialize(TaxiOrder taxiOrder, Type type, JsonSerializationContext context) {
            JsonObject to = new JsonObject();
            //primitives
            to.addProperty("id", taxiOrder.getId());
            to.addProperty("comment", taxiOrder.getComment());
            to.addProperty("musicStyle", taxiOrder.getMusicStyle());
            CarClass carClass = taxiOrder.getCarClass();
            to.addProperty("carClass", carClass == null ? null : carClass.getClassName());
            to.addProperty("executionDate", fmt.format(taxiOrder.getExecutionDate().getTime()));
            to.addProperty("registrationDate", fmt.format(taxiOrder.getRegistrationDate().getTime()));
            //lists
            to.add("features", context.serialize(taxiOrder.getFeatures()));
            //enums and other objects
            to.add("serviceType", context.serialize(taxiOrder.getServiceType()));
            to.add("driverSex", context.serialize(taxiOrder.getDriverSex()));
            to.add("paymentType", context.serialize(taxiOrder.getPaymentType()));

            return to;
        }
    }

    private static class AssembledRouteSerializer implements JsonSerializer<AssembledRoute> {

        private SimpleDateFormat fmt;

        public AssembledRouteSerializer(SimpleDateFormat fmt) {
            this.fmt = fmt;
        }

        @Override
        public JsonElement serialize(AssembledRoute route, Type type, JsonSerializationContext context) {
            JsonObject to = new JsonObject();
            //primitives
            to.addProperty("sourceAddress", route.getSource());
            to.addProperty("destinationAddress", route.getDestination());
            to.addProperty("totalPrice", route.getTotalPrice());
            to.addProperty("totalDistance", route.getTotalDistance());
            to.addProperty("finishedCars", route.getFinishedCars());
            to.addProperty("totalCars", route.getTotalCars());
            //objects

            return to;
        }
    }

    private static class FeatureSerializer implements JsonSerializer<Feature> {

        @Override
        public JsonElement serialize(Feature feature, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject fo = new JsonObject();
            fo.addProperty("featureName", feature.getName());
            return fo;
        }
    }

    private static class ServiceTypeSerializer implements JsonSerializer<ServiceType> {

        @Override
        public JsonElement serialize(ServiceType serviceType, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject st = new JsonObject();
            st.addProperty("name", serviceType.getName());
            st.addProperty("isDestinationLocationsChain", serviceType.isDestinationLocationsChain());
            st.addProperty("isDestinationRequired", serviceType.isDestinationRequired());
            st.addProperty("isMultipleDestinationLocations", serviceType.isMultipleDestinationLocations());
            st.addProperty("isMultipleSourceLocations", serviceType.isMultipleSourceLocations());
            return st;
        }
    }
}

