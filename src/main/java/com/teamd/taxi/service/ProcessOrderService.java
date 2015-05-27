package com.teamd.taxi.service;

import com.google.gson.JsonObject;
import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.*;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.service.email.MailService;
import com.teamd.taxi.service.email.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import javax.mail.MessagingException;
import java.util.*;

/**
 * Created by Іван on 23.05.2015.
 */
@Service
public class ProcessOrderService {

    @Autowired
    private MapService mapService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PriceCountService priceCountService;

    @Autowired
    private TaxiOrderService taxiOrderService;

    @Transactional
    public TaxiOrder getOrder(MultiValueMap<String, String> params, Driver driver) throws
            DriverHasActiveOrderException,
            TaxiOrderNotExist,
            DiscrepancyDriverAndOrderFeatureException,
            OrderBookedException, InvalidURLParamException {

        TaxiOrder taxiOrder;

        if (taxiOrderService.findCurrentOrderByDriverId(driver.getId()) != null) {
            throw new DriverHasActiveOrderException();
        }
        long id;

        try {
            List<String> s;
            if( (s = params.get("id")) != null) {
                id = Long.valueOf( s.get(0));
            }else throw new TaxiOrderNotExist();
        }catch (NumberFormatException e){
            throw new InvalidURLParamException();
        }

        if ((taxiOrder = taxiOrderService.findOneById(id)) == null) {
            throw new TaxiOrderNotExist();
        }

        if (!appropriateDriverAndOrderFeature(driver, taxiOrder)) {
            throw new DiscrepancyDriverAndOrderFeatureException();
        }
        testDestSourceParam(taxiOrder, driver, params);

        return taxiOrder;

    }
    @Transactional
    public JsonObject processOrder(String status, Driver driver) throws InfoNotFoundException, ItemNotFoundException {

        TaxiOrder taxiOrder;
        JsonObject to = new JsonObject();
        Integer driverId = driver.getId();
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driverId)) != null) {

            List<Route> routes = getChainForDriver(taxiOrder, driverId);
            System.out.println("START");
            long taxiOrderId = taxiOrder.getId();
            if (status.equals("refuse")) {///  for refuse
                for (Route r : routes) {
                    if (r.getStatus() == RouteStatus.ASSIGNED) {
                        to.addProperty("status", "REFUSED");
                        initRefuseStatus(r);
                    }
                }
                to.addProperty("orderStatus", "refused");
            } else if (status.equals("complete")) {/// wtite for complete
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.IN_PROGRESS)) {
                        initCompleteStatus(r);
                        r.setStatus(RouteStatus.COMPLETED);
                        Calendar calendar = Calendar.getInstance();
                        r.setCompletionTime(calendar);
                        to.addProperty("status", "COMPLETED");
                        to.addProperty("id", r.getId());
                        break;
                    }
                }
                //перевіряєм чи ланцюжок замовлення виконаниний повністю, всі повиггі бути COMPLETED
                to.addProperty("orderStatus", "complete");
                boolean allComplete = true;
                for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
                    // якщо в водій вже виконував роути з замовлення і вони були відхилені, тоді видаляєм списку з перевірки
                    Route route = it.next();
                    if (route.getStatus() == RouteStatus.REFUSED) {
                        it.remove();
                    } else if (route.getStatus() != RouteStatus.COMPLETED) {
                        allComplete = false;
                        to.addProperty("orderStatus", "continue");
                        break;
                    }
                }
                if(allComplete){
                    Float totalPrice = setPrice(taxiOrderId, driverId);
                    to.addProperty("totalPrice", totalPrice);
                }
            } else if (status.equals("inProgress")) {
                for (Route r : routes) {
                    if ((r.getStatus() == RouteStatus.ASSIGNED)) {
                        initInProgressStatus(r);
                        to.addProperty("status", "IN PROGRESS");
                        to.addProperty("id", r.getId());
                        to.addProperty("orderStatus", "continue");
                        break;
                    }
                }
            }
        } else {
            to.addProperty("orderStatus", "end");
        }
        return to;
    }

    private Float setPrice(Long taxiOrderId, Integer driverId) throws InfoNotFoundException, ItemNotFoundException {
        float totalPrice = 0;
        List<Float> listPrice;
        TaxiOrder taxiOrder = taxiOrderService.findOneById(taxiOrderId);
        List<Route> routes = getChainForDriver(taxiOrder, driverId);
        System.out.println("SIZE = "+taxiOrder.getRoutes());
        Boolean isChain = taxiOrder.getServiceType().isDestinationLocationsChain();
        if (isChain != null && isChain) {
            listPrice = priceCountService.countPriceForLastChainOrder(taxiOrderId, driverId);
            System.out.println("List Price  : "+listPrice);
            for (int i = 0; i < routes.size(); i++) {
                Route r = routes.get(i);
                Float price = listPrice.get(i);
                totalPrice += price;
                r.setTotalPrice(price);
                System.out.println("Driver : "+r.getDriver().getId()+
                        " Route status : "+r.getStatus()+
                        " Route price : "+r.getTotalPrice());
                routeService.saveRoute(r);
            }
        } else {
            long routeId = routes.get(routes.size() - 1).getId();
            Route r = routeService.getRouteById(routeId);
            System.out.println("ROUTE ID = = "+routeId+" SOURCE = "+r.getSourceAddress()+ " DEST = " +r.getDestinationAddress()+" COMPL TIME : "+r.getStatus());
            totalPrice = priceCountService.countPriceForSingleRouteOrder(routeId);
            r.setTotalPrice(totalPrice);
            routeService.setTotalPrice(totalPrice, r.getId());
        }
        System.out.println("Total Price : "+totalPrice);
        return (float)Math.round(totalPrice*100)/100;
    }

    @Transactional
    public Route newRoute(String dest, int driverId) throws NotFoundException, MapServiceNotAvailableException, TaxiOrderNotExist, NewRouteNotSupportForOrderException {

        TaxiOrder taxiOrder;
        if ((taxiOrder = taxiOrderService.findCurrentOrderByDriverId(driverId)) != null) {
            Boolean isChain = taxiOrder.getServiceType().isDestinationLocationsChain();
            if (isChain != null && isChain) {
                AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
                List<AssembledRoute> assembledRoutes = assembledOrder.getAssembledRoutes();
                if (assembledRoutes.get(0).getRoutes().size() == 1){
                    Float distance;
                    List<Route> routes = getChainForDriver(taxiOrder, driverId);
                    String source = routes.get(routes.size()-1).getDestinationAddress();
                    if ((distance = mapService.calculateDistanceInKilometers(source, dest)) != null) {
                        int routePosition = assembledOrder.getAssembledRoutes().size() + 1;
                        Route route = new Route();
                        route.setStatus(RouteStatus.ASSIGNED);
                        route.setSourceAddress(source);
                        route.setDestinationAddress(dest);
                        route.setCustomerLate(false);
                        Driver driver = driverService.getDriver(driverId);
                        route.setDriver(driver);
                        route.setOrder(taxiOrder);
                        route.setDistance(distance);
                        route.setChainPosition(routePosition);
                        routeService.saveRoute(route);
                        return route;
                    }else{
                        return null;
                    }
                }else{
                    throw new NewRouteNotSupportForOrderException();
                }
            } else {
                throw new NewRouteNotSupportForOrderException();
            }
        } else {
            throw new TaxiOrderNotExist();
        }
    }

    @Transactional
    public Calendar getTimeOfLastCompletionRouteInChain(TaxiOrder taxiOrder, int driverId) {
        List<Route> sortRoutes = getChainForDriver(taxiOrder, driverId);
        for (int i = 0; i < sortRoutes.size(); i++) {
            Route r = sortRoutes.get(i);
            if (driverId == r.getDriver().getId() && r.getStatus() == RouteStatus.IN_PROGRESS) {
                return null;
            } else if (driverId == r.getDriver().getId() && r.getStatus() == RouteStatus.ASSIGNED) {
                return sortRoutes.get(i - 1).getCompletionTime();
            }
        }
        return null;
    }

    @Transactional
    public String[] loadAddress(TaxiOrder taxiOrder, int driverId) {

        List<Route> routes = getChainForDriver(taxiOrder, driverId);
        for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
            Route route = it.next();
            if ((route.getStatus() == RouteStatus.COMPLETED) || (route.getStatus() == RouteStatus.REFUSED)) {
                it.remove();
            }
        }
        String[] addresses;
        if (taxiOrder.getServiceType().isDestinationRequired()){
            addresses = new String[routes.size() + 1];
            addresses[0] = routes.get(0).getSourceAddress();
            for (int i = 0; i < routes.size(); i++) {
                addresses[i + 1] = routes.get(i).getDestinationAddress();
            }
        }else{
            addresses = new String[1];
            addresses[0] = routes.get(0).getSourceAddress();
        }

        return addresses;
    }

    @Transactional
    public List<Route> getChainForDriver(TaxiOrder taxiOrder, int driverId) {
        System.out.println("Taxi Order ID = " + taxiOrder.getId() + "  Driver " + driverId);
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
        List<AssembledRoute> assembledRoutes = assembledOrder.getAssembledRoutes();
        List<Route> routes = new ArrayList<>();
        List<Route> result = new ArrayList<>();

        ServiceType serviceType = taxiOrder.getServiceType();

        if (serviceType.isDestinationRequired()) {
            System.out.println("Destination required ");
            //Taxi Asap, Advance
            Boolean isChain = serviceType.isDestinationLocationsChain();
            if ((isChain != null) && isChain) {
                for (int j = 0; j < assembledRoutes.size(); j++) {
                    routes = new ArrayList<>();
                    for (Route route : assembledRoutes.get(j).getRoutes()) {
                        Driver routeDriver = route.getDriver();
                        if ((routeDriver != null) && (driverId == routeDriver.getId())) {
                            routes.add(route);
                        }
                    }
                    System.out.println("Chain Driver : "+routes.get(routes.size()-1).getDriver().getId()+
                            " Route status : "+routes.get(routes.size()-1).getStatus()+
                            " Service type : "+taxiOrder.getServiceType().getName()+
                            " Source : "+routes.get(routes.size()-1).getSourceAddress());
                    result.add(routes.get(routes.size()-1));

                }
                System.out.println(result);
                return result;
            } else {
                // Convey corp emps, guest delivery
                if(serviceType.isMultipleSourceLocations()){

                    for (int j = 0; j < assembledRoutes.size(); j++) {
                        for (Route route : assembledRoutes.get(j).getRoutes()) {
                            Driver routeDriver = route.getDriver();
                            if ((routeDriver != null) && (driverId == routeDriver.getId())) {
                                System.out.println("Driver : "+route.getDriver().getId()+
                                        " Route status : "+route.getStatus()+
                                        " Service type : "+taxiOrder.getServiceType().getName());
                                routes.add(route);
                            }
                        }
                    }
                    Collections.sort(routes, new Comparator<Route>(){
                        @Override
                        public int compare(Route r1, Route r2) {
                            Calendar s1 = r1.getStartTime();
                            Calendar s2 = r2.getStartTime();
                            if (s1 == null) {
                                return 1;
                            } else if (s2 == null) {
                                return -1;
                            }
                            return s1.compareTo(s2);
                        }
                    });
                    if(!routes.isEmpty()){
                        result.add(routes.get(routes.size()-1));
                    }
                    return result;
                }else{
                    // Meet my guest, Cargo Taxi,
                    // sober, foodstuff
                    routes = new ArrayList<>();
                    for (Route route : assembledRoutes.get(0).getRoutes()) {
                        Driver routeDriver = route.getDriver();
                        if ((routeDriver != null) && (driverId == routeDriver.getId())) {
                            routes.add(route);
                        }
                    }
                    System.out.println("Driver : "+routes.get(routes.size()-1).getDriver().getId()+
                            " Route status : "+routes.get(routes.size()-1).getStatus()+
                            " Service type : "+taxiOrder.getServiceType().getName());
                    if(!routes.isEmpty()){
                        result.add(routes.get(routes.size()-1));
                    }
                    return result;
                }
            }
        } else {// Celebration taxi, Taxi for along term

            routes = new ArrayList<>();
            for (Route route : assembledRoutes.get(0).getRoutes()) {
                Driver routeDriver = route.getDriver();
                if ((routeDriver != null) && (driverId == routeDriver.getId())) {
                    routes.add(route);
                }
            }
            System.out.println("Driver : "+routes.get(routes.size()-1).getDriver().getId()+
                    " Route status : "+routes.get(routes.size()-1).getStatus()+
                    " Service type : "+taxiOrder.getServiceType().getName());
            if(!routes.isEmpty()){
                result.add(routes.get(routes.size()-1));
            }
            return result;
        }
    }

    private void testDestSourceParam(TaxiOrder taxiOrder, Driver driver, MultiValueMap<String, String> params) throws OrderBookedException, InvalidURLParamException {

        ServiceType serviceType = taxiOrder.getServiceType();
        if (serviceType.isDestinationRequired()) {
            System.out.println("Destination required ");
            //Taxi Asap, Advance
            if ((serviceType.isDestinationLocationsChain() != null) &&
                    serviceType.isDestinationLocationsChain()) {
                List<Route> freeRoutes = getFreeRouteForChainOrder(taxiOrder);
                System.out.println("Taxi Asap, Advance ");
                for (Route r : freeRoutes) {
                    initAssignStatus(driver, freeRoutes);
                }
            } else {
                // Cargo Taxi, Convey corp emps, Meet my guest,
                // sober, foodstuff, guest delivery
                System.out.println("Cargo Taxi, Convey corp emps, Meet my guest, sober, foodstuff, guest delivery");
                List<Route> freeRoutes = getFreeRouteForOrder(taxiOrder);
                List<String> sourceL;
                List<String> destL;
                String source, dest;
                if( (sourceL = params.get("source")) != null ){
                    source = sourceL.get(0);
                }else
                    throw new InvalidURLParamException();
                if ( (destL = params.get("dest")) != null){
                    dest = destL.get(0);
                }else
                    throw new InvalidURLParamException();

                if (source != null && dest != null) {
                    for (Route r : freeRoutes) {
                        if (r.getSourceAddress().equals(source) && r.getDestinationAddress().equals(dest)) {
                            List<Route> freeRoute = new ArrayList<>();
                            freeRoute.add(r);
                            initAssignStatus(driver, freeRoute);
                            break;
                        }
                    }
                } else {
                    throw new InvalidURLParamException();
                }
            }
        } else {// Celebration taxi, Taxi for along term
            System.out.println("Celebration taxi,  Taxi for along term ");
            List<Route> freeRoutes = getFreeRouteForOrder(taxiOrder);
            String source;
            List<String> sList;
            if( (sList = params.get("source")) != null ){
                source = sList.get(0);
            }else
                throw new InvalidURLParamException();

            if (source != null) {
                for (Route r : freeRoutes) {
                    if (r.getSourceAddress().equals(source)) {
                        List<Route> freeRoute = new ArrayList<>();
                        freeRoute.add(r);
                        initAssignStatus(driver, freeRoute);
                        break;
                    }
                }
            } else {
                throw new InvalidURLParamException();
            }
        }
    }

    private List<Route> getFreeRouteForOrder(TaxiOrder taxiOrder) throws OrderBookedException {
        List<Route> freeRoute = new ArrayList<>();
        boolean isFree = false;
        for (Route route : taxiOrder.getRoutes()) {
            if (route.getStatus() == RouteStatus.QUEUED) {
                isFree = true;
                freeRoute.add(route);
            }
        }
        if (!isFree) {
            throw new OrderBookedException();
        }
        return freeRoute;
    }

    private List<Route> getFreeRouteForChainOrder(TaxiOrder taxiOrder) throws OrderBookedException {
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(taxiOrder);
        List<AssembledRoute> assRoutes = assembledOrder.getAssembledRoutes();
        List<Route> routes = new ArrayList<>(assRoutes.size());

        for (int j = 0; j < assRoutes.size(); j++) {
            boolean busy = false;
            for (Route route : assRoutes.get(j).getRoutes()) {
                if (route.getStatus() == RouteStatus.QUEUED) {
                    routes.add(route);
                    busy = true;
                    break;
                }
            }
            if (busy == false) {
                throw new OrderBookedException();
            }
        }
        return routes;
    }

    private boolean appropriateDriverAndOrderFeature(Driver driver, TaxiOrder taxiOrder) {
        List<Feature> drvFeatures = driver.getFeatures();
        drvFeatures.addAll(driver.getCar().getFeatures());
        List<Feature> toFeature = taxiOrder.getFeatures();
        return drvFeatures.containsAll(toFeature);
    }

    private void initAssignStatus(Driver driver, List<Route> routes) {
        TaxiOrder taxiOrder = taxiOrderService.findOneById(routes.get(0).getOrder().getId());
        System.out.println(" /n ASSIGNED STATUS START");
        for (int i = 0; i < routes.size(); i++) {
            routeService.updateRouteAssigned(RouteStatus.ASSIGNED, driver, routes.get(i).getId());
        }
        System.out.println(" /n ASSIGNED STATUS FINISH");

        Object[] objs = {taxiOrder.getExecutionDate(), routes.get(0).getSourceAddress()};
        notifyClientAboutOrderStatusChange(routes.get(0), Notification.ASSIGNED, objs);
    }

    private void initInProgressStatus(Route r) {

        Calendar calendar = Calendar.getInstance();
        System.out.println(" /n IN PROGRESS STATUS START ");
        routeService.updateRouteInProgress(RouteStatus.IN_PROGRESS, calendar, r.getId());
        System.out.println(" /n IN PROGRESS STATUS FINISH");

        Object[] objs = {r.getSourceAddress()};
        notifyClientAboutOrderStatusChange(r, Notification.IN_PROGRESS, objs);
    }

    private void initCompleteStatus(Route r) {

        System.out.println(" /n COMPLETE STATUS START");
        Calendar calendar = Calendar.getInstance();
        routeService.updateRouteCompleted(RouteStatus.COMPLETED, calendar, r.getId());
        System.out.println(" /n COMPLETE STATUS FINISH");

        Object[] objs = {r.getSourceAddress(), r.getCompletionTime()};
        notifyClientAboutOrderStatusChange(r, Notification.COMPLETED, objs);
    }

    private void initRefuseStatus(Route r) {
        System.out.println(" /n REFUSE STATUS START");
        Calendar calendar = Calendar.getInstance();
        routeService.updateRouteRefused(RouteStatus.REFUSED, calendar, r.getId());
        System.out.println(" /n REFUSE STATUS FINISH");

        Object[] objs = {r.getSourceAddress()};
        notifyClientAboutOrderStatusChange(r, Notification.REFUSED, objs);
    }

    private void notifyClientAboutOrderStatusChange(Route r, Notification routeStatus, Object... objs){
        TaxiOrder taxiOrder = r.getOrder();
        User user = taxiOrder.getCustomer();
        try {
            mailService.sendNotification(user.getEmail(), routeStatus, objs);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
