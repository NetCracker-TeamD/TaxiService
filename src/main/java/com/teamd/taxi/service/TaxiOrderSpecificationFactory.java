package com.teamd.taxi.service;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.RouteStatus;
import com.teamd.taxi.entity.TaxiOrder;
import org.apache.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.Calendar;

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
                return criteriaBuilder.greaterThan(root.<Calendar>get("executionDate"), calendar);
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
                return criteriaBuilder.greaterThan(root.<Calendar>get("registrationDate"), calendar);
            }
        };
    }
    public Specification<TaxiOrder> driverIdEqual(final int id){
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
    public Specification<TaxiOrder> statusRouteEqual(final RouteStatus routeStatus){
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TaxiOrder, Route> routeJoin = root.join("routes", JoinType.INNER);
                return cb.equal(routeJoin.get("status"), routeStatus);
            }
        };
    }
    public Specification<TaxiOrder> serviceTypeEqual(final int idServiceType){
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("serviceType"), idServiceType);
            }
        };
    }
    public Specification<TaxiOrder> taxiOrderEqual(final int id){
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"),id);
            }
        };
    }
    public Specification<TaxiOrder> sourceOrDestinationAddressLike(final String address){
        return new Specification<TaxiOrder>() {
            @Override
            public Predicate toPredicate(Root<TaxiOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TaxiOrder, Route> routeJoin = root.join("routes", JoinType.INNER);
                Predicate sourcePredicate=cb.like(routeJoin.<String>get("sourceAddress"), "%" + address+ "%");
                Predicate destinationPredicate=cb.like(routeJoin.<String>get("destinationAddress"), "%" + address+ "%");
                Predicate srcOrDstPredicate=cb.or(sourcePredicate,destinationPredicate);
                return srcOrDstPredicate;
            }
        };
    }
}
