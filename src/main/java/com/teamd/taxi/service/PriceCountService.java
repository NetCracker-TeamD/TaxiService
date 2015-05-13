package com.teamd.taxi.service;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.InfoNotFoundException;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.persistence.repository.InfoRepository;
import com.teamd.taxi.persistence.repository.RouteRepository;
import com.teamd.taxi.persistence.repository.TariffRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Calendar.*;

import java.util.*;

/**
 * Created by Олег on 11.05.2015.
 */
@Service
public class PriceCountService {

    private static final String IDLE_FREE_TIME_NAME = "idle_free_time";

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private InfoRepository infoRepository;

    @Transactional
    private Float countRoutePrice(
            Route route,
            List<TariffByTime> dayOfYearTariffs,
            List<TariffByTime> dayOfWeekTariffs,
            List<TariffByTime> timeOfDayTariffs) {
        Calendar start = route.getStartTime();
        Calendar complete = route.getCompletionTime();
        //розбиваємо по дням
        List<TimeCoeffPair> pairs = splitByDays(new TimeCoeffPair(start, complete, 1f));
        //тарифікація по дням року
        for (TariffByTime dayOfYearTariff : dayOfYearTariffs) {
            for (TimeCoeffPair pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_YEAR);
                if (dayOfYearTariff.getFrom().get(DAY_OF_YEAR) >= dayOfYear
                        && dayOfYear < dayOfYearTariff.getTo().get(DAY_OF_YEAR)) {
                    pair.coeff *= dayOfYearTariff.getPrice();
                }
            }
        }
        //тарифікація по дням тижня
        for (TariffByTime dayOfWeekTariff : dayOfWeekTariffs) {
            for (TimeCoeffPair pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_WEEK);
                if (dayOfWeekTariff.getFrom().get(DAY_OF_WEEK) >= dayOfYear
                        && dayOfYear < dayOfWeekTariff.getTo().get(DAY_OF_WEEK)) {
                    pair.coeff *= dayOfWeekTariff.getPrice();
                }
            }
        }
        //
        List<TimeCoeffPair> processed = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            List<TimeCoeffPair> nonProcessed = new ArrayList<>();
            nonProcessed.add(pairs.get(i));
            Queue<TariffByTime> tariffByTimeQueue = new LinkedList<>(timeOfDayTariffs);
            //проходимо по всім тарифам
            while (!tariffByTimeQueue.isEmpty()) {
                TariffByTime nextTariff = tariffByTimeQueue.poll();
                //намагаємось знайти відповідність серед необроблених
                //часових інтервалів
                for (int j = 0; j < nonProcessed.size(); j++) {
                    TimeCoeffPair nextPair = nonProcessed.get(i);
                    boolean leftBoundEntry = nextTariff.getFrom().compareTo(nextPair.from) >= 0;
                    boolean rightBoundEntry = nextTariff.getTo().compareTo(nextPair.to) < 0;
                    if (leftBoundEntry && rightBoundEntry) {
                        //розбиваємо проміжок на 3
                        TimeCoeffPair[] leftSplit = nextPair.splitWithOneSecondDifference(nextTariff.getFrom());
                        TimeCoeffPair[] rightSplit = leftSplit[1].splitWithOneSecondDifference(nextTariff.getTo());
                        //оновлюємо ціну
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        //заносимо в потрібний список
                        processed.add(rightSplit[0]);
                        nonProcessed.add(leftSplit[0]);
                        nonProcessed.add(rightSplit[1]);
                    } else if (leftBoundEntry) {
                        TimeCoeffPair[] leftSplit = nextPair.splitWithOneSecondDifference(nextTariff.getFrom());
                        leftSplit[1].coeff *= nextTariff.getPrice();
                        processed.add(leftSplit[1]);
                        nonProcessed.add(leftSplit[0]);
                    } else if (rightBoundEntry) {
                        TimeCoeffPair[] rightSplit = nextPair.splitWithOneSecondDifference(nextTariff.getTo());
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        processed.add(rightSplit[0]);
                        processed.add(rightSplit[1]);
                    }
                    if (leftBoundEntry || rightBoundEntry) {
                        nonProcessed.remove(j);
                        break;
                    }
                }
            }
            processed.addAll(nonProcessed);
        }
        //лишилось підбити підсумки :)
        double totalDuration = complete.getTimeInMillis() - start.getTimeInMillis();
        double price = 0f;
        float tariffMultiplier;
        float orderMultiplier;
        ServiceType type = route.getOrder().getServiceType();
        if (type.getPriceByDistance() == null) {
            tariffMultiplier = type.getPriceByTime();
            //виражаємо час у годинах
            orderMultiplier = (float) (totalDuration / (60 * 60 * 1000));
        } else {
            tariffMultiplier = type.getPriceByDistance();
            orderMultiplier = route.getDistance();
        }
        for (TimeCoeffPair part : processed) {
            price += part.coeff * tariffMultiplier * orderMultiplier * (part.getDuration() / totalDuration);
        }
        //враховуємо автомобіль
        price *= route.getDriver().getCar().getCarClass().getPriceCoefficient();
        //додаємо ціну за фічі
        List<Feature> features = route.getOrder().getFeatures();
        for (Feature feature : features) {
            price += feature.getPrice();
        }
        return (float) price;
    }

    private float countStartIdlePrice(Route route) throws InfoNotFoundException {
        if (!route.isCustomerLate()) {
            return 0;
        }
        TaxiOrder order = route.getOrder();
        Calendar executionDate = order.getExecutionDate();
        Calendar startDate = route.getStartTime();
        Info idleFreeTimeInfo = infoRepository.findOne(IDLE_FREE_TIME_NAME);
        if (idleFreeTimeInfo == null) {
            throw new InfoNotFoundException(IDLE_FREE_TIME_NAME + " not found");
        }

        long freeTimeMilis = Long.parseLong(idleFreeTimeInfo.getValue()) * 60 * 1000;
        long difference = route.getStartTime().getTimeInMillis()
                - order.getExecutionDate().getTimeInMillis() - freeTimeMilis;
        if (difference > 0) {
            float idlePriceCoeff = order.getCarClass().getIdlePriceCoefficient();
            return (float) (idlePriceCoeff * (difference / (1000 * 60.0)));
        }
        return 0;
    }

    @Transactional
    public Float countPriceForSingleRouteOrder(long routeId) throws ItemNotFoundException, InfoNotFoundException {
        Route route = routeRepository.findOne(routeId);
        if (route == null) {
            throw new ItemNotFoundException();
        }
        TaxiOrder order = route.getOrder();
        ServiceType serviceType = order.getServiceType();
        if (serviceType.isDestinationLocationsChain()) {
            //якщо оформлено замовлення такого виду, потрібно рахувати
            //ціну на все замовлення, а не на окремий маршрут
            throw new RuntimeException("does not make sense");
        }
        List<TariffByTime> dayOfYearTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_YEAR);
        List<TariffByTime> dayOfWeekTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_WEEK);
        List<TariffByTime> timeOfDayTariffs = tariffRepository.findByTariffType(TariffType.TIME_OF_DAY);
        return countRoutePrice(route, dayOfYearTariffs, dayOfWeekTariffs, timeOfDayTariffs)
                + countStartIdlePrice(route);
    }

    private static class TimeCoeffPair {
        public Calendar from;
        public Calendar to;
        public float coeff;

        public TimeCoeffPair(Calendar from, Calendar to, float coeff) {
            this.from = from;
            this.to = to;
            this.coeff = coeff;
        }

        public TimeCoeffPair[] splitWithOneSecondDifference(Calendar between) {
            TimeCoeffPair[] pair = new TimeCoeffPair[2];

            pair[0] = new TimeCoeffPair(from, between, coeff);

            between = (Calendar) between.clone();
            between.add(SECOND, 1);
            pair[1] = new TimeCoeffPair(between, to, coeff);

            return pair;
        }

        public double getDuration() {
            return to.getTimeInMillis() - from.getTimeInMillis();
        }
    }

    private static List<TimeCoeffPair> splitByDays(TimeCoeffPair pair) {
        LinkedList<TimeCoeffPair> pairs = new LinkedList<>();
        pairs.add(pair);

        Calendar from = pair.from;
        Calendar to = pair.to;
        while (from.get(DAY_OF_YEAR) != to.get(DAY_OF_YEAR)) {
            Calendar next = (Calendar) from.clone();
            next.set(HOUR_OF_DAY, from.getActualMaximum(HOUR_OF_DAY));
            next.set(MINUTE, from.getActualMaximum(MINUTE));
            next.set(SECOND, from.getActualMaximum(SECOND));

            pairs.pollLast();
            pairs.add(new TimeCoeffPair(from, next, 1f));

            next = (Calendar) next.clone();
            next.add(SECOND, 1);
            pairs.add(new TimeCoeffPair(next, to, 1f));

            from = next;
        }

        return pairs;
    }
}
