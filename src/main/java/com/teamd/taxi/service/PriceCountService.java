package com.teamd.taxi.service;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.InfoNotFoundException;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.models.TaxiOrderForm;
import com.teamd.taxi.persistence.repository.*;
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
    private TariffByTimeRepository tariffRepository;

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private TaxiOrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    private Float countRoutePrice(
            Route route,
            List<TariffByTime> dayOfYearTariffs,
            List<TariffByTime> dayOfWeekTariffs,
            List<TariffByTime> timeOfDayTariffs) {
        Calendar start = (Calendar) route.getStartTime().clone();
        Calendar complete = (Calendar) route.getCompletionTime().clone();
        //розбиваємо по дням
        List<TimeCoeffPair> pairs = splitByDays(new TimeCoeffPair(start, complete, 1f));
        //тарифікація по дням року
        for (TariffByTime dayOfYearTariff : dayOfYearTariffs) {
            for (TimeCoeffPair pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_YEAR);
                if (dayOfYearTariff.getFrom().get(DAY_OF_YEAR) >= dayOfYear
                        && dayOfYear <= dayOfYearTariff.getTo().get(DAY_OF_YEAR)) {
                    pair.coeff *= dayOfYearTariff.getPrice();
                }
            }
        }
        //тарифікація по дням тижня
        for (TariffByTime dayOfWeekTariff : dayOfWeekTariffs) {
            for (TimeCoeffPair pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_WEEK);
                if (dayOfWeekTariff.getFrom().get(DAY_OF_WEEK) >= dayOfYear
                        && dayOfYear <= dayOfWeekTariff.getTo().get(DAY_OF_WEEK)) {
                    pair.coeff *= dayOfWeekTariff.getPrice();
                }
            }
        }
        //тарифікація по годинам
        List<TimeCoeffPair> processed = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            List<TimeCoeffPair> nonProcessed = new ArrayList<>();
            TimeCoeffPair pair = pairs.get(i);
            nonProcessed.add(pair);
            Queue<TariffByTime> tariffByTimeQueue = new LinkedList<>(timeOfDayTariffs);
            //проходимо по всім тарифам
            while (!tariffByTimeQueue.isEmpty()) {
                TariffByTime nextTariff = shiftToDay(tariffByTimeQueue.poll(), pair.from);
                //намагаємось знайти відповідність серед необроблених
                //часових інтервалів
                for (int j = 0; j < nonProcessed.size(); j++) {
                    TimeCoeffPair nextPair = nonProcessed.get(j);
                    boolean leftBoundEntry = nextTariff.getFrom().compareTo(nextPair.from) >= 0
                            && nextTariff.getFrom().compareTo(nextPair.to) <= 0;
                    boolean rightBoundEntry = nextTariff.getTo().compareTo(nextPair.from) >= 0
                            && nextTariff.getTo().compareTo(nextPair.to) <= 0;
                    if (leftBoundEntry && rightBoundEntry) {
                        //розбиваємо проміжок на 3
                        TimeCoeffPair[] leftSplit = nextPair.splitWithOneSecondDifference(nextTariff.getFrom(), false);
                        TimeCoeffPair[] rightSplit = leftSplit[1].splitWithOneSecondDifference(nextTariff.getTo(), true);
                        //оновлюємо ціну
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        //заносимо в потрібний список
                        processed.add(rightSplit[0]);
                        nonProcessed.add(leftSplit[0]);
                        nonProcessed.add(rightSplit[1]);
                    } else if (leftBoundEntry) {
                        TimeCoeffPair[] leftSplit = nextPair.splitWithOneSecondDifference(nextTariff.getFrom(), false);
                        leftSplit[1].coeff *= nextTariff.getPrice();
                        processed.add(leftSplit[1]);
                        nonProcessed.add(leftSplit[0]);
                    } else if (rightBoundEntry) {
                        TimeCoeffPair[] rightSplit = nextPair.splitWithOneSecondDifference(nextTariff.getTo(), true);
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        processed.add(rightSplit[0]);
                        nonProcessed.add(rightSplit[1]);
                    }
                    if (leftBoundEntry || rightBoundEntry) {
                        nonProcessed.remove(j);
                        break; //більше одного тарифу бути не може
                        //оскільки вони не перетинаються
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
        Float priceByDistance = type.getPriceByDistance();
        if (priceByDistance == null) {
            tariffMultiplier = type.getPriceByTime();
            //виражаємо час у годинах
            orderMultiplier = (float) (totalDuration / (60 * 60 * 1000));
        } else {
            tariffMultiplier = priceByDistance;
            orderMultiplier = route.getDistance();
        }
        for (TimeCoeffPair part : processed) {
            price += part.coeff * tariffMultiplier * orderMultiplier * (part.getDuration() / totalDuration);
        }
        //враховуємо автомобіль
        price *= route.getDriver().getCar().getCarClass().getPriceCoefficient();
        return (float) price;
    }

    private TariffByTime shiftToDay(TariffByTime tariff, Calendar sample) {
        int year = sample.get(YEAR);
        int dayOfYear = sample.get(DAY_OF_YEAR);

        Calendar from = (Calendar) tariff.getFrom().clone();
        from.set(YEAR, year);
        from.set(DAY_OF_YEAR, dayOfYear);

        Calendar to = (Calendar) tariff.getTo().clone();
        to.set(YEAR, year);
        to.set(DAY_OF_YEAR, dayOfYear);
        return new TariffByTime(null, from, to, tariff.getPrice(), tariff.getTariffType());
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

        long freeTimeMillis = Long.parseLong(idleFreeTimeInfo.getValue()) * 60 * 1000;
        long difference = startDate.getTimeInMillis()
                - executionDate.getTimeInMillis() - freeTimeMillis;
        if (difference > 0) {
            float idlePriceCoeff = order.getCarClass().getIdlePriceCoefficient();
            return (float) (idlePriceCoeff * (difference / (1000 * 60.0)));
        }
        return 0;
    }

    private float getGroupDiscountForUser(User user) {
        if (user.getUserRole() == UserRole.ROLE_ANONYMOUS) {
            return 0.0f;
        }
        List<GroupList> groupLists = user.getGroups();
        float max = 0.0f;
        for (GroupList groupList : groupLists) {
            Float discount = groupList.getUserGroup().getDiscount();
            if (discount != null && discount > max) {
                max = discount;
            }
        }
        return max;
    }

    @Transactional
    public Float countPriceForSingleRouteOrder(long routeId) throws ItemNotFoundException, InfoNotFoundException {
        Route route = routeRepository.findOne(routeId);
        if (route == null) {
            throw new ItemNotFoundException();
        }
        TaxiOrder order = route.getOrder();
        ServiceType serviceType = order.getServiceType();
        Boolean chain = serviceType.isDestinationLocationsChain();
        if (chain != null && chain) {
            //якщо оформлено замовлення такого виду, потрібно рахувати
            //ціну на все замовлення, а не на окремий маршрут
            throw new RuntimeException("does not make sense");
        }
        List<TariffByTime> dayOfYearTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_YEAR);
        List<TariffByTime> dayOfWeekTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_WEEK);
        List<TariffByTime> timeOfDayTariffs = tariffRepository.findByTariffType(TariffType.TIME_OF_DAY);
        float price = countRoutePrice(route, dayOfYearTariffs, dayOfWeekTariffs, timeOfDayTariffs)
                + countStartIdlePrice(route);
        //скидка по групі
        float groupDiscount = getGroupDiscountForUser(order.getCustomer());
        //ціна за фічі
        List<Feature> features = route.getOrder().getFeatures();
        float featurePrice = 0f;
        for (Feature feature : features) {
            featurePrice += feature.getPrice();
        }
        return Math.max(price * (1 - groupDiscount), serviceType.getMinPrice()) + featurePrice;
    }

    private void filterByDriverId(List<Route> all, int driverId) {
        for (Iterator<Route> routeIterator = all.iterator(); routeIterator.hasNext(); ) {
            Route next = routeIterator.next();
            Driver driver = next.getDriver();
            if (driver == null || driver.getId() != driverId || next.getStatus() == RouteStatus.REFUSED) {
                routeIterator.remove();
            }
        }
    }

    private List<Route> getLastChainByDriverId(TaxiOrder order, int driverId) {
        AssembledOrder assembledOrder = AssembledOrder.assembleOrder(order);
        List<Route> routes = new ArrayList<>();
        for (AssembledRoute assembledRoute : assembledOrder.getAssembledRoutes()) {
            List<Route> allRoutes = assembledRoute.getRoutes();
            filterByDriverId(allRoutes, driverId);
            routes.add(allRoutes.get(allRoutes.size() - 1));
        }
        return routes;
    }

    @Transactional
    public List<Float> countPriceForLastChainOrder(long orderId, int driverId) throws ItemNotFoundException, InfoNotFoundException {
        TaxiOrder order = orderRepository.findOne(orderId);
        if (order == null) {
            throw new ItemNotFoundException();
        }
        Boolean chain = order.getServiceType().isDestinationLocationsChain();
        if (chain == null || !chain) {
            throw new RuntimeException("does not make sense");
        }
        List<Route> routes = getLastChainByDriverId(order, driverId);
        if (routes.isEmpty()) {
            return null;
        }
        //групова скидка
        float groupDiscount = getGroupDiscountForUser(order.getCustomer());
        //фічі
        List<Feature> features = order.getFeatures();
        float featurePrice = 0f;
        for (Feature feature : features) {
            featurePrice += feature.getPrice();
        }
        //тарифікація по часу
        List<TariffByTime> dayOfYearTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_YEAR);
        List<TariffByTime> dayOfWeekTariffs = tariffRepository.findByTariffType(TariffType.DAY_OF_WEEK);
        List<TariffByTime> timeOfDayTariffs = tariffRepository.findByTariffType(TariffType.TIME_OF_DAY);
        List<Float> routePrices = new ArrayList<>(routes.size());
        //ціна на перший маршрут
        Float price = countStartIdlePrice(routes.get(0));
        price += countRoutePrice(routes.get(0), dayOfYearTariffs, dayOfWeekTariffs, timeOfDayTariffs);
        routePrices.add(price * (1 - groupDiscount));
        //решта
        float idleCoeff = order.getCarClass().getIdlePriceCoefficient();
        for (int i = 1; i < routes.size(); i++) {
            price = countRoutePrice(routes.get(i), dayOfYearTariffs, dayOfWeekTariffs, timeOfDayTariffs);
            long difference = routes.get(i).getStartTime().getTimeInMillis()
                    - routes.get(i - 1).getCompletionTime().getTimeInMillis();
            price += (float) (idleCoeff * (difference / (1000 * 60.0)));
            routePrices.add(price * (1 - groupDiscount));
        }
        //перевіряємо, чи не вийшла ціна замовлення менше ніж minPrice
        float totalPrice = 0f;
        for (Float routePrice : routePrices) {
            totalPrice += routePrice;
        }
        float minPrice = order.getServiceType().getMinPrice();
        //якщо вийшло, то ділимо micPrice порівну між всіма роутами
        if (totalPrice < minPrice) {
            float part = minPrice / routePrices.size();
            for (int i = 0; i < routePrices.size(); i++) {
                routePrices.set(i, part);
            }
        }
        //додаємо ціну за фічі
        for (int i = 0; i < routePrices.size(); i++) {
            float routePrice = routePrices.get(i);
            routePrices.set(i, routePrice + featurePrice);
        }
        System.out.println("Counted prices: " + routePrices);
        return routePrices;
    }

    @Transactional
    public float approximateOrderPrice(TaxiOrder order, Long userId) {
        List<Route> routes = order.getRoutes();
        //цена за фичи
        List<Feature> features = order.getFeatures();
        float featurePrice = 0;
        for (Feature feature : features) {
            featurePrice += feature.getPrice();
        }
        featurePrice *= routes.size();
        //скидка за группы
        float groupDiscount = 1;
        if (userId != null) {
            groupDiscount = getGroupDiscountForUser(userRepository.findOne(userId));
        }
        //цена за маршруты
        float routePrice = 0;
        ServiceType serviceType = order.getServiceType();
        CarClass carClass = order.getCarClass();
        //считать что-то можно только если указан пункт назначения
        if (serviceType.isDestinationRequired()) {
            for (Route route : routes) {
                routePrice += route.getDistance()
                        * serviceType.getPriceByDistance()
                        * carClass.getPriceCoefficient();
            }
        }
        return Math.max(routePrice * groupDiscount, serviceType.getMinPrice()) + featurePrice;
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

        public TimeCoeffPair[] splitWithOneSecondDifference(Calendar between, boolean secondToTheRight) {
            TimeCoeffPair[] pair = new TimeCoeffPair[2];

            pair[0] = new TimeCoeffPair(from, (Calendar) between.clone(), coeff);
            pair[1] = new TimeCoeffPair((Calendar) between.clone(), to, coeff);
            if (secondToTheRight) {
                pair[1].from.add(SECOND, 1);
            } else {
                pair[0].to.add(SECOND, -1);
            }
            return pair;
        }

        public double getDuration() {
            return to.getTimeInMillis() - from.getTimeInMillis();
        }

        @Override
        public String toString() {
            return "TimeCoeffPair{" +
                    "from=" + from.getTime() +
                    ", to=" + to.getTime() +
                    ", coeff=" + coeff +
                    '}';
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
