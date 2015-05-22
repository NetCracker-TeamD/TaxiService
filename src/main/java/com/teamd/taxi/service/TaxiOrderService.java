package com.teamd.taxi.service;

import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import com.teamd.taxi.exception.NotCompatibleException;
import com.teamd.taxi.exception.OrderUpdatingException;
import com.teamd.taxi.exception.PropertyNotFoundException;
import com.teamd.taxi.models.TaxiOrderForm;
import com.teamd.taxi.persistence.repository.RouteRepository;
import com.teamd.taxi.persistence.repository.ServiceTypeRepository;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Anton on 02.05.2015.
 */
@Service
public class TaxiOrderService {

    @Autowired
    private TaxiOrderRepository orderRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private RandomStringGenerator stringGenerator;

    @Autowired
    private MapService mapService;

    private static final int KEY_LENGTH = 20;

    private static final Logger logger = Logger.getLogger(TaxiOrderService.class);

    @Transactional
    public Page<TaxiOrder> findTaxiOrderByUser(long id, Pageable pageable) {
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        Page<TaxiOrder> to = orderRepository.findByUserId(id, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public Page<TaxiOrder> findTaxiOrderByDriver(Specification<TaxiOrder> specs, Pageable pageable) {
        if (orderRepository == null) {
            logger.error("orderRepository is null");
        }
        Page<TaxiOrder> to = orderRepository.findAll(specs, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public Page<TaxiOrder> findAll(Pageable pageable) {
        Page<TaxiOrder> to = orderRepository.findAll(pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public Page<TaxiOrder> findAll(Specification<TaxiOrder> spec, Pageable pageable) {
        Page<TaxiOrder> to = orderRepository.findAll(spec, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public TaxiOrder findOneById(long id) {
        System.out.println("ID : " + id);
        TaxiOrder order = orderRepository.findOne(id);
        if (order != null) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
            Hibernate.initialize(order.getServiceType());
        } else {
            System.out.println(" ORDER IS NULL =  0 " + order);
        }
        return order;
    }

    @Transactional
    public TaxiOrder fillOrder(TaxiOrderForm form, User user)
            throws PropertyNotFoundException, NotFoundException,
            MapServiceNotAvailableException, NotCompatibleException {
        TaxiOrder order = new TaxiOrder();
        ServiceType serviceType = form.getServiceType();
        //проверка совместимости класса автомобиля и сервиса
        CarClass carClass = form.getCarClass();
        List<CarClass> allowedCarClasses = serviceType.getAllowedCarClasses();
        if ((carClass == null && allowedCarClasses.size() != 0) ||
                (carClass != null && !allowedCarClasses.contains(form.getCarClass()))) {
            //или класс не указан, хотя должен быть
            //или указан неверно
            throw new NotCompatibleException();
        }
        //проверка совместимости фич и сервиса
        List<Feature> orderFeatureList = form.getFeatures();
        if (orderFeatureList != null) {
            List<Feature> allowedFeatures = serviceType.getAllowedFeatures();
            for (Feature feature : orderFeatureList) {
                if (!allowedFeatures.contains(feature)) {
                    throw new NotCompatibleException();
                }
            }
        }
        //создание прототипов маршрутов и проверка адрессов
        List<Route> routes = new ArrayList<>();
        Boolean isChain = serviceType.isDestinationLocationsChain();
        if (isChain != null && isChain) {
            List<String> intermediate = form.getIntermediate();
            if (intermediate == null) {
                intermediate = new ArrayList<>();
            }
            intermediate.add(0, form.getSource().get(0));
            intermediate.add(form.getDestination().get(0));
            for (int i = 1; i < intermediate.size(); i++) {
                Route route = new Route(
                        null,
                        RouteStatus.QUEUED,
                        intermediate.get(i - 1),
                        intermediate.get(i),
                        false
                );
                route.setChainPosition(i);
                route.setDistance(mapService.calculateDistanceInKilometers(
                        route.getSourceAddress(),
                        route.getDestinationAddress()
                ));
                routes.add(route);
            }
        } else if (serviceType.isMultipleSourceLocations()) {
            List<String> sources = form.getSource();
            String destination = form.getDestination().get(0);
            for (String source : sources) {
                Route route = new Route(null, RouteStatus.QUEUED, source, destination, false);
                route.setDistance(
                        mapService.calculateDistanceInKilometers(
                                route.getSourceAddress(),
                                route.getDestinationAddress()
                        ));
                routes.add(route);
            }
        } else {
            String source = form.getSource().get(0);
            mapService.checkAddress(source);
            routes.add(new Route(null, RouteStatus.QUEUED, source, null, false));
        }
        //размножаем их до необх. количества автомобилей
        if (serviceType.isMultipleSourceLocations()) {
            List<Route> multiplied = new ArrayList<>();
            List<Integer> amounts = form.getCarsAmount();
            if (amounts.size() != routes.size()) {
                throw new PropertyNotFoundException("not enough car amounts: expected - " +
                        routes.size() + ", actual - " + amounts.size());
            }
            for (int i = 0; i < routes.size(); i++) {
                int amount = amounts.get(i);
                Route prototype = routes.get(i);
                multiplied.addAll(makeClones(prototype, amount));
            }
            routes = multiplied;
        }
        //TODO: isDestRequired
        else {
            int amount = form.getCarsAmount().get(0);
            List<Route> multipled = new ArrayList<>();
            for (Route route : routes) {
                multipled.addAll(makeClones(route, amount));
            }
            routes = multipled;
        }
        //финальное заполнение и сохранение обьекта
        String driverSexString = form.getDriverSex();
        Sex sex = "ANY".equals(driverSexString)
                ? null : Sex.valueOf(driverSexString);
        order.setDriverSex(sex);
        order.setCarClass(form.getCarClass());
        order.setCustomer(user);
        order.setServiceType(serviceType);
        order.setRegistrationDate(Calendar.getInstance());
        order.setPaymentType(form.getPaymentType());
        order.setExecutionDate(form.getExecDate());
        order.setFeatures(form.getFeatures());
        order.setRoutes(routes);
        return order;
    }

    @Transactional
    public TaxiOrder createNewTaxiOrder(TaxiOrderForm form, User user)
            throws NotCompatibleException,
            MapServiceNotAvailableException,
            PropertyNotFoundException,
            NotFoundException {
        TaxiOrder order = fillOrder(form, user);
        List<Route> routes = order.getRoutes();
        order.setRoutes(null);
        //создание ключа для анон. пользователя
        if (user.getUserRole() == UserRole.ROLE_ANONYMOUS) {
            String secretKey = stringGenerator.generateString(KEY_LENGTH);
            order.setSecretViewKey(secretKey);
        }
        //необходимо отдельно сохранить заказ и все маршруты
        order = orderRepository.save(order);
        for (Route route : routes) {
            route.setOrder(order);
        }
        routes = routeRepository.save(routes);
        order.setRoutes(routes);
        return order;
    }

    @Transactional
    public void setUpdating(long orderId) throws OrderUpdatingException {
        TaxiOrder order = orderRepository.findOne(orderId);
        List<Route> routes = order.getRoutes();
        for (Route route : routes) {
            if (route.getStatus() != RouteStatus.QUEUED) {
                throw new OrderUpdatingException("not updatable", orderId);
            }
            route.setStatus(RouteStatus.UPDATING);
        }
        orderRepository.save(order);
    }

    @Transactional
    public void cancelUpdating(long orderId) throws OrderUpdatingException {
        TaxiOrder order = orderRepository.findOne(orderId);
        List<Route> routes = order.getRoutes();
        for (Route route : routes) {
            if (route.getStatus() != RouteStatus.UPDATING) {
                throw new OrderUpdatingException("not under updating", orderId);
            }
            route.setStatus(RouteStatus.QUEUED);
        }
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(long orderId) throws OrderUpdatingException {
        TaxiOrder order = orderRepository.findOne(orderId);
        List<Route> routes = order.getRoutes();
        for (Route route : routes) {
            if (route.getStatus() != RouteStatus.QUEUED) {
                throw new OrderUpdatingException("not cancelable", orderId);
            }
            route.setStatus(RouteStatus.CANCELED);
        }
        orderRepository.save(order);
    }

    @Transactional
    public TaxiOrder updateTaxiOrder(long orderId, TaxiOrderForm form) throws NotCompatibleException,
            PropertyNotFoundException, NotFoundException, MapServiceNotAvailableException,
            OrderUpdatingException {
        TaxiOrder oldOrder = orderRepository.findOne(orderId);
        for (Route route : oldOrder.getRoutes()) {
            if (route.getStatus() != RouteStatus.UPDATING) {
                throw new OrderUpdatingException("not under updating", orderId);
            }
        }
        TaxiOrder orderWithUpdates = fillOrder(form, null);
        //копируем данные
        oldOrder.setDriverSex(orderWithUpdates.getDriverSex());
        oldOrder.setCarClass(orderWithUpdates.getCarClass());
        oldOrder.setRegistrationDate(Calendar.getInstance());
        oldOrder.setPaymentType(orderWithUpdates.getPaymentType());
        oldOrder.setExecutionDate(orderWithUpdates.getExecutionDate());
        oldOrder.setFeatures(orderWithUpdates.getFeatures());
        //удаляем старые маршруты
        List<Route> oldRoutes = oldOrder.getRoutes();
        routeRepository.delete(oldRoutes);
        //добавляем новые
        List<Route> routes = orderWithUpdates.getRoutes();
        for (Route route : routes) {
            route.setOrder(oldOrder);
        }
        oldOrder.setRoutes(routes);
        //сохраняем
        oldOrder = orderRepository.save(oldOrder);
        return oldOrder;
    }

    private List<Route> makeClones(Route prototype, int amount) {
        List<Route> multiplied = new ArrayList<>();
        while (amount-- > 0) {
            multiplied.add(makeClone(prototype));
        }
        return multiplied;
    }

    private Route makeClone(Route route) {
        Route clone = new Route();
        clone.setSourceAddress(route.getSourceAddress());
        clone.setDestinationAddress(route.getDestinationAddress());
        clone.setStatus(route.getStatus());
        clone.setCustomerLate(route.isCustomerLate());
        clone.setDistance(route.getDistance());
        clone.setChainPosition(route.getChainPosition());
        return clone;
    }

    @Transactional
    public TaxiOrder findCurrentOrderByDriverId(int id) {
        List<TaxiOrder> orders = orderRepository.findCurrentOrderByDriverId(id);
        if (orders.isEmpty()) {
            return null;
        }
        TaxiOrder order = orders.get(0);
        System.out.println("READ FEATURES  ");
        Hibernate.initialize(order.getFeatures());
        System.out.println("READ ROUTES  ");
        Hibernate.initialize(order.getRoutes());
        System.out.println("READ TYPE  ");
        Hibernate.initialize(order.getServiceType());
        System.out.println("READ COMPLETE  ");
        return order;
    }

}