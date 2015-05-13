package com.teamd.taxi.service;

import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;
import com.teamd.taxi.persistence.repository.TaxiOrderRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.Calendar;
import java.util.List;

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
