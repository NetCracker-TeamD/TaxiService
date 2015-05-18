package com.teamd.taxi.controllers.driver;

import com.google.gson.*;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.models.PageDetails;
import com.teamd.taxi.models.PagingLink;
import com.teamd.taxi.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * Created by Іван on 02.05.2015.
 */
@Controller
@RequestMapping("/driver")
public class QueueController {

    public final static String SORT_BY = "executionDate";
    public final static int PAGE_SIZE = 20;
    private Gson gson;

    @Autowired
    @Qualifier("dateFormatter")
    private SimpleDateFormat dateFormat;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private PagingLinksGenerator linksGenerator;

    @Autowired
    private DriverService driverService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Autowired
    private TaxiOrderSpecificationFactory taxiOrderSpecificationFactory;

    private static int currentDriverID = 6;

    private static final Logger log = Logger.getLogger(QueueController.class);


    @RequestMapping(value = "/loadQueue", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String loadQueue(Pageable pageable, @RequestParam MultiValueMap<String, String> params) {
        log.info("Received params: " + params);
        Driver driver = driverService.getDriver(currentDriverID);
        pageable = new PageRequest(pageable.getPageNumber(), PAGE_SIZE);
        //вибрані сервіси з TRUE значенням інші з FALSE
        Map<ServiceType, Boolean> selectedTypes = getSelectedTypes(params);
        //повертає всі id`s фіч для даного водія і його машини
        List<Integer> featureIds = getIdsAllFeature(driver.getId());

        log.info("Featured = " + featureIds);
        //запит на доступні замовлення
        Specifications<TaxiOrder> spec = where(new OrderSpec(featureIds, driver.getCar().getCarClass()))
                .and(new RouteSpec());
        Specification<TaxiOrder> additional = resolveSpecification(selectedTypes);
        if (additional != null) {
            spec = spec.and(additional);
        }

        Page<TaxiOrder> orders = taxiOrderService.findAll(spec, pageable);
        List<TaxiOrder> content = orders.getContent();

        HashMap<String, Object> returnValue = new HashMap<>();
        if (content.size() != 0) {
            //remove unnecessary orders
            for (TaxiOrder taxiOrder : content) {
                List<Route> routes = taxiOrder.getRoutes();
                for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
                    Route next = it.next();
                    if (next.getStatus() != RouteStatus.QUEUED) {
                        it.remove();
                    }
                }
            }
            log.info("Order = " + content);
            //assembling orders
            List<AssembledOrder> assembledOrders = new ArrayList<>(content.size());
            for (TaxiOrder taxiOrder : content) {
                assembledOrders.add(AssembledOrder.assembleOrder(taxiOrder));
            }
            log.info("AssembledOrder = " + assembledOrders);

            //generating links for paging
            UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodName(
                    QueueController.class, "viewCurrentOrder", null, null, null
            );
            addSelectedServices(builder, selectedTypes);
            List<PagingLink> links = linksGenerator.generateLinks(orders, builder);
            //send data to the receiver
            returnValue.put("orders", assembledOrders);
            returnValue.put("pageDetails", new PageDetails(orders));
            returnValue.put("links", links);
            returnValue.put("status", "ok");
        } else {
            returnValue.put("status", "notFound");
        }
        return getGson().toJson(returnValue);
    }

    private void addSelectedServices(UriComponentsBuilder builder, Map<ServiceType, Boolean> types) {
        for (Map.Entry<ServiceType, Boolean> entry : types.entrySet()) {
            if (entry.getValue()) {
                builder.queryParam(entry.getKey().getId() + "", "on");
            }
        }
    }

    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public String viewCurrentOrder(Pageable pageable, Model model, @RequestParam MultiValueMap<String, String> params) {
        log.info(params);
        pageable = new PageRequest(pageable.getPageNumber(), PAGE_SIZE);
        //вибрані сервіси з TRUE значенням інші з FALSE
        Map<ServiceType, Boolean> selectedTypes = getSelectedTypes(params);

        Driver driver = driverService.getDriver(currentDriverID);
        TaxiOrder taxiOrder;
        log.info("PARAMETER "+taxiOrderService.findCurrentOrderByDriverId(driver.getId()));
        if(!driver.isAtWork() || ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driver.getId())) != null) ){
            model.addAttribute("activeOrder", true);
            model.addAttribute("pageable", pageable);
            model.addAttribute("selectedServices", selectedTypes);
            System.out.println("CAN NOT RECEIVED ORDER");
            return "driver/drv-queue";
        }
        model.addAttribute("activeOrder", false);
        model.addAttribute("pageable", pageable);
        model.addAttribute("selectedServices", selectedTypes);
        return "driver/drv-queue";
    }

    private Specification<TaxiOrder> resolveSpecification(Map<ServiceType, Boolean> selectedTypes) {
        if (selectedTypes.containsValue(true)) {
            List<Integer> serviceIds = new ArrayList<>();
            for (Map.Entry<ServiceType, Boolean> st : selectedTypes.entrySet()) {
                if (st.getValue()) {
                    serviceIds.add(st.getKey().getId());
                }
            }
            return taxiOrderSpecificationFactory.serviceTypeIn(serviceIds);
        }
        return null;
    }

    private List<Integer> getIdsAllFeature(int driverID) {
        Driver driver = driverService.getDriver(driverID);
        Car car = driver.getCar();
        List<Feature> merged = new ArrayList<>(driver.getFeatures());
        merged.addAll(car.getFeatures());
        System.out.print("/nFEATURE = ");
        for( Feature f :  car.getFeatures()){
            System.out.print(f.getId()+" __ ");
        }

        List<Integer> featureIds = new ArrayList<>();
        for (Feature f : merged) {
            featureIds.add(f.getId());
        }
        return featureIds;
    }

    private Map<ServiceType, Boolean> getSelectedTypes(MultiValueMap<String, String> params) {
        log.info("Selected Service = " + params);
        Map<ServiceType, Boolean> selected = new TreeMap<>(
                new Comparator<ServiceType>() {
                    @Override
                    public int compare(ServiceType o1, ServiceType o2) {
                        if (o1.getId() > o2.getId()) {
                            return 1;
                        } else return -1;
                    }
                });
        List<ServiceType> serviceTypes = serviceTypeService.findAll();
        for (ServiceType serviceType : serviceTypes) {
            boolean isSelect = params.keySet().contains(String.valueOf(serviceType.getId()));
            selected.put(serviceType, isSelect);
        }
        //do work
        return selected;
    }


    private static class OrderSpec implements Specification<TaxiOrder> {

        private List<Integer> featureIds;
        private CarClass carClass;

        public OrderSpec(List<Integer> featureIds, CarClass carClass) {
            this.carClass = carClass;
            this.featureIds = featureIds;
        }

        @Override
        public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
            cq.distinct(true);
            Subquery<Long> taxiOrderIdSubquery = cq.subquery(Long.class);
            Root<TaxiOrder> subRoot = taxiOrderIdSubquery.from(TaxiOrder.class);
            taxiOrderIdSubquery.select(subRoot.<Long>get("id"));
            Join<TaxiOrder, Feature> subFeatureJoin = subRoot.join("features");
            taxiOrderIdSubquery.where(cb.not(
                    subFeatureJoin.get("id").in(featureIds)
            ));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 1);

            return cb.and(
                    cb.not(root.<Long>get("id").in(taxiOrderIdSubquery)),
                    cb.lessThan(root.<Calendar>get("executionDate"), calendar),
                    cb.equal(root.<CarClass>get("carClass"), carClass)
            );
        }
    }

    private static class RouteSpec implements Specification<TaxiOrder> {
        @Override
        public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
            Join<TaxiOrder, Route> routes = root.join("routes");
            return criteriaBuilder.equal(routes.<RouteStatus>get("status"), RouteStatus.QUEUED);
        }
    }


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
            to.addProperty("musicStyle", taxiOrder.getMusicStyle());
            to.addProperty("executionDate", fmt.format(taxiOrder.getExecutionDate().getTime()));
            //lists
            to.add("features", context.serialize(taxiOrder.getFeatures()));
            //enums and other objects
            to.add("serviceType", context.serialize(taxiOrder.getServiceType()));
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
            to.addProperty("totalDistance", route.getTotalDistance());
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
            return st;
        }
    }
}
