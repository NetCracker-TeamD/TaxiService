package com.teamd.taxi.controllers.user;

import com.google.gson.*;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.models.PagingLink;
import com.teamd.taxi.service.PagingLinksGenerator;
import com.teamd.taxi.service.TaxiOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import sun.net.util.URLUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/user")
public class UserHistoryController {

    @Autowired
    private TaxiOrderService orderService;

    @Autowired
    private PagingLinksGenerator linksGenerator;

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

    private void addSortingParams(UriComponentsBuilder builder, MultiValueMap<String, String> params) {
        List<String> sortingParams = params.get("sort");
        if (sortingParams != null) {
            for (String sortingParam : sortingParams) {
                builder.queryParam("sort", sortingParam);
            }
        }
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

    @RequestMapping(value = "/loadHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String loadHistory(Pageable pageable, Sort sort, @RequestParam MultiValueMap<String, String> params) {
        logger.info("pageable = " + pageable);
        logger.info("All params = " + params);
        //retrieving data from database
        Page<TaxiOrder> page = orderService.findAll(
                new PageRequest(pageable.getPageNumber(), PAGE_SIZE, pageable.getSort())
        );
        List<TaxiOrder> content = page.getContent();
        //assembling orders
        List<AssembledOrder> assembledOrders = new ArrayList<>(content.size());
        for (TaxiOrder taxiOrder : content) {
            assembledOrders.add(AssembledOrder.assembleOrder(taxiOrder));
        }
        //generating links for paging
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodName(UserHistoryController.class, "viewHistory", null, null);
        addSortingParams(builder, params);
        List<PagingLink> links = linksGenerator.generateLinks(page, builder);
        //send data to the receiver
        HashMap<String, Object> returnValue = new HashMap<>();
        returnValue.put("orders", assembledOrders);
        returnValue.put("pageDetails", new PageDetails(page));
        returnValue.put("links", links);
        returnValue.put("status", "OK");

        return getGson().toJson(returnValue);
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

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Pageable pageable, Model model) {
        logger.info(pageable);
        logger.info(model);

        Sort sort = pageable.getSort();
        if (sort != null) {
            List<String> sorts = getStringBySort(sort);
            model.addAttribute("sorts", sorts);
        }
        model.addAttribute("pageable", pageable);
        return "user/user-history";
    }

    @RequestMapping(value = "/old", method = RequestMethod.GET)
    public String viewOldHistory(Model model, HttpServletRequest request) {
        int page = 0;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }
        int idUser = 1;
        int numberOfRows = 7;
        String sort = "id";
        if (request.getParameter("sort") != null) {
            switch (request.getParameter("sort")) {
                case "date":
                    sort = "executionDate";
                    break;
                case "id":
                    sort = "id";
                    break;
            }
        }
        Pageable pageable = new PageRequest(page, numberOfRows, Sort.Direction.ASC, sort);
        Page<TaxiOrder> orderList = orderService.findAll(pageable);
        if (orderList == null) {
            //redirect error page
        }
        List<TaxiOrder> orders = orderList.getContent();
        List<Float> prices = new ArrayList<Float>();
        for (TaxiOrder order : orders) {
            float price = 0.0f;
            for (Route route : order.getRoutes()) {
                if (route.getTotalPrice() != null) {
                    price += route.getTotalPrice();
                }
            }
            prices.add(price);
        }
        model.addAttribute("orderList", orderList.getContent());
        model.addAttribute("prices", prices);
        model.addAttribute("pages", orderList.getTotalPages());
        return "user-history";
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
            to.addProperty("carClass", taxiOrder.getCarClass().getClassName());
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

    private static class PageDetails {

        private long totalElements;
        private int totalPage;
        private int elementsNumber;
        private int pageNumber;

        public PageDetails(Page<?> page) {
            totalElements = page.getTotalElements();
            totalPage = page.getTotalPages();
            elementsNumber = page.getNumberOfElements();
            pageNumber = page.getNumber();
        }
    }
}

