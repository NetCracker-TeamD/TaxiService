package com.teamd.taxi.models;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;

import java.util.*;

public class AssembledOrder {

    private TaxiOrder order;

    private LinkedList<AssembledRoute> assembledRoutes;

    private Float totalPrice;

    private boolean complete;

    public AssembledOrder(TaxiOrder order, LinkedList<AssembledRoute> assembledRoutes) {
        this.order = order;
        this.assembledRoutes = assembledRoutes;
        totalPrice = 0f;
        for (AssembledRoute assembledRoute : assembledRoutes) {
            Float assembledRoutePrice = assembledRoute.getTotalPrice();
            if (assembledRoutePrice == null) {
                totalPrice = null;
                break;
            }
            totalPrice += assembledRoutePrice;
        }
        complete = true;
        for (AssembledRoute assembledRoute : assembledRoutes) {
            if (assembledRoute.getTotalCars() != assembledRoute.getFinishedCars()) {
                complete = false;
                break;
            }
        }
    }

    public static AssembledOrder assembleOrder(TaxiOrder order) {
        LinkedList<AssembledRoute> routes = assembleRoutes(order);

        ServiceType type = order.getServiceType();
        Boolean chain = type.isDestinationLocationsChain();
        //Переупорядочиваем маршруты, так чтобы они стояли друг за другом
        if (chain != null && chain) {
            Collections.sort(routes, new Comparator<AssembledRoute>() {
                public int compare(AssembledRoute a, AssembledRoute b) {
                    return Integer.compare(a.getChainPosition(), b.getChainPosition());
                }
            });
        }
        return new AssembledOrder(order, routes);
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    private static LinkedList<AssembledRoute> assembleRoutes(TaxiOrder order) {
        LinkedList<AssembledRoute> routes = new LinkedList<>();
        List<Route> originalRoutes = order.getRoutes();
        while (!originalRoutes.isEmpty()) {
            List<Route> assembled = new ArrayList<>();
            Route sample = originalRoutes.remove(0);
            String source = sample.getSourceAddress();
            String destination = sample.getDestinationAddress();
            for (int i = 0; i < originalRoutes.size(); i++) {
                Route candidate = originalRoutes.get(i);
                String candidateSource = candidate.getSourceAddress();
                String candidateDestination = candidate.getDestinationAddress();
                if (candidateSource.equals(source) && //точки отправления равны
                        ((destination == null && candidateDestination == null) //точки назначения или обе отсутствуют
                                || candidateDestination.equals(destination))) { //или равны
                    assembled.add(candidate);
                    originalRoutes.remove(i);
                    i--;
                }
            }
            assembled.add(sample);
            routes.add(new AssembledRoute(source, destination, sample.getChainPosition(), assembled));
        }
        return routes;
    }

    public boolean isComplete() {
        return complete;
    }

    public LinkedList<AssembledRoute> getAssembledRoutes() {
        return assembledRoutes;
    }

    public TaxiOrder getOrder() {
        return order;
    }
}
