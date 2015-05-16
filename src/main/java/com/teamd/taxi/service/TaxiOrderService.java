package com.teamd.taxi.service;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.AddressNotFoundException;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import com.teamd.taxi.exception.NotCompatibleException;
import com.teamd.taxi.exception.PropertyNotFoundException;
import com.teamd.taxi.models.TaxiOrderForm;
import com.teamd.taxi.persistence.repository.RouteRepository;
import com.teamd.taxi.persistence.repository.ServiceTypeRepository;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
        if (spec == null) {
            return findAll(pageable);
        }
        Page<TaxiOrder> to = orderRepository.findAll(spec, pageable);
        for (TaxiOrder order : to.getContent()) {
            Hibernate.initialize(order.getFeatures());
            Hibernate.initialize(order.getRoutes());
        }
        return to;
    }

    @Transactional
    public TaxiOrder findOneById(long id) {
        TaxiOrder order = orderRepository.findOne(id);
        Hibernate.initialize(order.getFeatures());
        Hibernate.initialize(order.getRoutes());
        Hibernate.initialize(order.getServiceType());
        return order;
    }

    @Transactional
    public TaxiOrder createNewTaxiOrder(TaxiOrderForm form, User user) throws NotCompatibleException, MapServiceNotAvailableException, AddressNotFoundException, PropertyNotFoundException {
        TaxiOrder order = new TaxiOrder();
        ServiceType serviceType = form.getServiceType();
        //проверка совместимости класса автомобиля и сервиса
        CarClass carClass = form.getCarClass();
        if (carClass != null && !serviceType.getAllowedCarClasses().contains(form.getCarClass())) {
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
        List<String> addressesToCheck = new ArrayList<>();
        Boolean isChain = serviceType.isDestinationLocationsChain();
        if (isChain != null && isChain) {
            List<String> intermediate = form.getIntermediate();
            if (intermediate == null) {
                intermediate = new ArrayList<>();
            }
            intermediate.add(0, form.getSource().get(0));
            intermediate.add(form.getDestination().get(0));
            addressesToCheck = intermediate;
            for (int i = 1; i < intermediate.size(); i++) {
                routes.add(new Route(
                        null,
                        RouteStatus.QUEUED,
                        intermediate.get(i - 1),
                        intermediate.get(i),
                        false
                ));
            }
        } else if (serviceType.isMultipleSourceLocations()) {
            List<String> sources = form.getSource();
            String destination = form.getDestination().get(0);
            for (String source : sources) {
                routes.add(new Route(null, RouteStatus.QUEUED, source, destination, false));
            }
            addressesToCheck.addAll(sources);
            addressesToCheck.add(destination);
        } else {
            String source = form.getSource().get(0);
            routes.add(new Route(null, RouteStatus.QUEUED, source, null, false));
            addressesToCheck.add(source);
        }
        //проверка адрессов
        for (String address : addressesToCheck) {
            if (!mapService.isExists(address)) {
                throw new AddressNotFoundException(address);
            }
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
        } else {
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
        //создание ключа для анон. пользователя
        if (user.getUserRole() == UserRole.ROLE_ANONYMOUS) {
            String secretKey = stringGenerator.generateString(KEY_LENGTH);
            order.setSecretViewKey(secretKey);
        }
        order = orderRepository.save(order);

        logger.info("saved order: " + order);

        for (Route route : routes) {
            route.setOrder(order);
        }
        routes = routeRepository.save(routes);
        order.setRoutes(routes);
        return order;
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
        return clone;
    }

    @Transactional
    public TaxiOrder findCurrentOrderByDriverId(int id) {
        List<TaxiOrder> orders = orderRepository.findCurrentOrderByDriverId(id);
        if(orders.isEmpty()){
            return null;
        }
        TaxiOrder order = orders.get(0);
        Hibernate.initialize(order.getFeatures());
        Hibernate.initialize(order.getRoutes());
        Hibernate.initialize(order.getServiceType());
        return order;
    }

}