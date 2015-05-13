package com.teamd.taxi.service;

import com.teamd.taxi.entity.TaxiOrder;
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
                return criteriaBuilder.greaterThan(root.<Calendar>get("registrationDate"), calendar);
            }
        };
    }
}
