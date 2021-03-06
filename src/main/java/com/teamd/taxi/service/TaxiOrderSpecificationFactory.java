package com.teamd.taxi.service;

import com.teamd.taxi.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.Calendar;
import java.util.*;

@Service
public class TaxiOrderSpecificationFactory {


    public Specification<TaxiOrder> registrationDateLessThan(final Calendar calendar) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThan(root.<Calendar>get("registrationDate"), calendar);
            }
        };
    }

    public Specification<TaxiOrder> registrationDateGreaterThan(final Calendar calendar) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThan(root.<Calendar>get("registrationDate"), calendar);
            }
        };
    }

    public Specification<TaxiOrder> executionDateLessThan(final Calendar calendar) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThan(root.<Calendar>get("executionDate"), calendar);
            }
        };
    }

    public Specification<TaxiOrder> executionDateGreaterThan(final Calendar calendar) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThan(root.<Calendar>get("executionDate"), calendar);
            }
        };
    }

    public Specification<TaxiOrder> userIdEqual(final long userId) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Join<TaxiOrder, User> customer = root.join("customer");
                return criteriaBuilder.equal(customer.<Long>get("id"), userId);
            }
        };
    }

    public Specification<TaxiOrder> driverIdEqual(final int id) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TaxiOrder, Route> routeJoin = root.join("routes", JoinType.INNER);
                Join<Route, Driver> driverJoin = routeJoin.join("driver");
                return cb.equal(driverJoin.get("id"), id);
            }
        };
    }

    public Specification<TaxiOrder> statusRouteOr(final RouteStatus routeStatus1,final RouteStatus routeStatus2) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TaxiOrder, Route> routeJoin = root.join("routes", JoinType.INNER);
                Predicate predicate1 = cb.equal(routeJoin.get("status"), routeStatus1);
                Predicate predicate2 = cb.equal(routeJoin.get("status"), routeStatus2);
                return cb.or(predicate1,predicate2);
            }
        };
    }

    public Specification<TaxiOrder> serviceTypeEqual(final int idServiceType) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("serviceType"), idServiceType);
            }
        };
    }

    public Specification<TaxiOrder> taxiOrderEqual(final int id) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"), id);
            }
        };
    }

    public Specification<TaxiOrder> sourceOrDestinationAddressLike(final String address) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TaxiOrder, Route> routeJoin = root.join("routes", JoinType.INNER);
                Predicate sourcePredicate = cb.like(routeJoin.<String>get("sourceAddress"), "%" + address + "%");
                Predicate destinationPredicate = cb.like(routeJoin.<String>get("destinationAddress"), "%" + address + "%");
                Predicate srcOrDstPredicate = cb.or(sourcePredicate, destinationPredicate);
                return srcOrDstPredicate;
            }
        };
    }

    public Specification<TaxiOrder> serviceTypeIn(final List<Integer> serviceTypeIds) {
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join<TaxiOrder, ServiceType> service = root.join("serviceType");
                return cb.isTrue(service.<Integer>get("id").in(serviceTypeIds));
            }
        };
    }

}
