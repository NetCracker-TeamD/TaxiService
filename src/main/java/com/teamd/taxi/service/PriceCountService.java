package com.teamd.taxi.service;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.exception.InfoNotFoundException;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.models.AssembledOrder;
import com.teamd.taxi.models.AssembledRoute;
import com.teamd.taxi.models.Price;
import com.teamd.taxi.persistence.repository.*;
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

    private static final String REFUSED_ORDER_PENALTY = "refused_order_penalty";

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

    @Autowired
    private BlackListItemRepository blackListItemRepository;

    @Transactional
    private Float countRoutePrice(
            Route route,
            List<TariffByTime> dayOfYearTariffs,
            List<TariffByTime> dayOfWeekTariffs,
            List<TariffByTime> timeOfDayTariffs) {
        Calendar start = (Calendar) route.getStartTime().clone();
        Calendar complete = (Calendar) route.getCompletionTime().clone();
        //розбиваємо по дням
        List<Interval> pairs = splitByDays(new Interval(start, complete, 1f));
        //тарифікація по дням року
        for (TariffByTime dayOfYearTariff : dayOfYearTariffs) {
            for (Interval pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_YEAR);
                if (dayOfYearTariff.getFrom().get(DAY_OF_YEAR) >= dayOfYear
                        && dayOfYear <= dayOfYearTariff.getTo().get(DAY_OF_YEAR)) {
                    pair.coeff *= dayOfYearTariff.getPrice();
                }
            }
        }
        //тарифікація по дням тижня
        for (TariffByTime dayOfWeekTariff : dayOfWeekTariffs) {
            for (Interval pair : pairs) {
                int dayOfYear = pair.from.get(DAY_OF_WEEK);
                if (dayOfWeekTariff.getFrom().get(DAY_OF_WEEK) >= dayOfYear
                        && dayOfYear <= dayOfWeekTariff.getTo().get(DAY_OF_WEEK)) {
                    pair.coeff *= dayOfWeekTariff.getPrice();
                }
            }
        }
        //тарифікація по годинам
        List<Interval> processed = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            List<Interval> nonProcessed = new ArrayList<>();
            Interval pair = pairs.get(i);
            nonProcessed.add(pair);
            Queue<TariffByTime> tariffByTimeQueue = new LinkedList<>(timeOfDayTariffs);
            //проходимо по всім тарифам
            while (!tariffByTimeQueue.isEmpty()) {
                TariffByTime nextTariff = shiftToDay(tariffByTimeQueue.poll(), pair.from);
                //намагаємось знайти відповідність серед необроблених
                //часових інтервалів
                for (int j = 0; j < nonProcessed.size(); j++) {
                    Interval nextInterval = nonProcessed.get(j);
                    Calendar tariffFrom = nextTariff.getFrom();
                    Calendar tariffTo = nextTariff.getTo();
                    boolean leftBoundEntry = tariffFrom.compareTo(nextInterval.from) >= 0
                            && tariffFrom.compareTo(nextInterval.to) <= 0;
                    boolean rightBoundEntry = tariffTo.compareTo(nextInterval.from) >= 0
                            && tariffTo.compareTo(nextInterval.to) <= 0;
                    boolean isProcessed = leftBoundEntry || rightBoundEntry;
                    /** legend:
                     * () - interval
                     * [] - tariff
                     */
                    // (____[____]____)
                    if (leftBoundEntry && rightBoundEntry) {
                        //розбиваємо проміжок на 3
                        Interval[] leftSplit = nextInterval.splitWithOneSecondDifference(nextTariff.getFrom(), false);
                        Interval[] rightSplit = leftSplit[1].splitWithOneSecondDifference(nextTariff.getTo(), true);
                        //оновлюємо ціну
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        //заносимо в потрібний список
                        processed.add(rightSplit[0]);
                        nonProcessed.add(leftSplit[0]);
                        nonProcessed.add(rightSplit[1]);
                    } //(____[____)____]
                    else if (leftBoundEntry) {
                        Interval[] leftSplit = nextInterval.splitWithOneSecondDifference(nextTariff.getFrom(), false);
                        leftSplit[1].coeff *= nextTariff.getPrice();
                        processed.add(leftSplit[1]);
                        nonProcessed.add(leftSplit[0]);
                    } // [____(____]____)
                    else if (rightBoundEntry) {
                        Interval[] rightSplit = nextInterval.splitWithOneSecondDifference(nextTariff.getTo(), true);
                        rightSplit[0].coeff *= nextTariff.getPrice();
                        processed.add(rightSplit[0]);
                        nonProcessed.add(rightSplit[1]);
                    } //[____(____)____]
                    else if (tariffFrom.compareTo(nextInterval.from) < 0
                            && tariffTo.compareTo(nextInterval.to) > 0) {
                        isProcessed = true;
                        nextInterval.coeff *= nextTariff.getPrice();
                        processed.add(nextInterval);
                    } //else ()[] or []()
                    if (isProcessed) {
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
        System.out.println("Resulted intervals: " + processed);
        for (Interval part : processed) {
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
        //штраф
        BlackListItem blackListItem = order.getBlackListItem();
        if (blackListItem != null) {
            Info penalty = infoRepository.findOne(REFUSED_ORDER_PENALTY);
            if (penalty != null) {
                //делим штраф поровну на все маршруты
                price += blackListItem.getMultiplier()
                        * Double.parseDouble(penalty.getValue()) / order.getRoutes().size();
            }
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
        //штраф
        BlackListItem blackListItem = order.getBlackListItem();
        if (blackListItem != null) {
            Info penalty = infoRepository.findOne(REFUSED_ORDER_PENALTY);
            if (penalty != null) {
                float penaltyPerRoute = blackListItem.getMultiplier()
                        * Float.parseFloat(penalty.getValue()) / order.getRoutes().size();
                for (int i = 0; i < routePrices.size(); i++) {
                    float routePrice = routePrices.get(i);
                    routePrices.set(i, routePrice + penaltyPerRoute);
                }
            }
        }
        System.out.println("Counted prices: " + routePrices);
        return routePrices;
    }

    @Transactional
    public Price approximateOrderPrice(TaxiOrder order, Long userId) {
        Price price = new Price();
        List<Route> routes = order.getRoutes();
        //цена за фичи
        List<Feature> features = order.getFeatures();
        float featurePrice = 0;
        if (features != null) {
            for (Feature feature : features) {
                featurePrice += feature.getPrice();
            }
        }
        featurePrice *= routes.size();
        price.setFeaturePrice(featurePrice);
        //скидка за группы
        float groupDiscount = 0;
        if (userId != null) {
            groupDiscount = getGroupDiscountForUser(userRepository.findOne(userId));
        }
        //цена за маршруты
        float routesPrice = 0;
        ServiceType serviceType = order.getServiceType();
        CarClass carClass = order.getCarClass();
        //считать что-то можно только если указан пункт назначения
        if (serviceType.isDestinationRequired()) {
            for (Route route : routes) {
                routesPrice += route.getDistance()
                        * serviceType.getPriceByDistance()
                        * carClass.getPriceCoefficient();
            }
        }
        float priceWithDiscount = routesPrice * (1 - groupDiscount);
        float minPrice = serviceType.getMinPrice();
        if (priceWithDiscount < minPrice) {
            price.setOriginalPrice(minPrice);
        } else {
            price.setOriginalPrice(routesPrice);
            price.setPriceWithDiscount(priceWithDiscount);
        }

        if (userId != null) {
            Info penalty = infoRepository.findOne(REFUSED_ORDER_PENALTY);
            if (penalty != null) {
                float penaltyValue = Float.parseFloat(penalty.getValue());
                price.setPenaltyPrice(penaltyValue * blackListItemRepository.countByUserIdAndPayedTrue(userId));
            }
        }

        return price;
    }

    private static class Interval {
        public Calendar from;
        public Calendar to;
        public float coeff;

        public Interval(Calendar from, Calendar to, float coeff) {
            this.from = from;
            this.to = to;
            this.coeff = coeff;
        }

        public Interval[] splitWithOneSecondDifference(Calendar between, boolean secondToTheRight) {
            Interval[] pair = new Interval[2];

            pair[0] = new Interval(from, (Calendar) between.clone(), coeff);
            pair[1] = new Interval((Calendar) between.clone(), to, coeff);
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
            return "Interval{" +
                    "from=" + from.getTime() +
                    ", to=" + to.getTime() +
                    ", coeff=" + coeff +
                    '}';
        }
    }

    private static List<Interval> splitByDays(Interval pair) {
        LinkedList<Interval> pairs = new LinkedList<>();
        pairs.add(pair);

        Calendar from = pair.from;
        Calendar to = pair.to;
        while (from.get(DAY_OF_YEAR) != to.get(DAY_OF_YEAR)) {
            Calendar next = (Calendar) from.clone();
            next.set(HOUR_OF_DAY, from.getActualMaximum(HOUR_OF_DAY));
            next.set(MINUTE, from.getActualMaximum(MINUTE));
            next.set(SECOND, from.getActualMaximum(SECOND));

            pairs.pollLast();
            pairs.add(new Interval(from, next, 1f));

            next = (Calendar) next.clone();
            next.add(SECOND, 1);
            pairs.add(new Interval(next, to, 1f));

            from = next;
        }

        return pairs;
    }
}
