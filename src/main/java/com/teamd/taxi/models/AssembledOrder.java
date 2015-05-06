package com.teamd.taxi.models;

import com.teamd.taxi.entity.Route;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TaxiOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
            LinkedList<AssembledRoute> reordered = new LinkedList<>();
            AssembledRoute globalPivot = routes.pollFirst();
            reordered.add(globalPivot);
            //достраиваем вправо от ведущего
            AssembledRoute pivot = globalPivot;
            boolean found = false;
            do {
                for (Iterator<AssembledRoute> it = routes.iterator(); it.hasNext(); ) {
                    AssembledRoute element = it.next();
                    if (element.getSource().equals(pivot.getDestination())) {
                        reordered.add(element);
                        pivot = element;
                        it.remove();
                        found = true;
                        break;
                    }
                }
            } while (found);
            //достраиваем влево от ведущего
            pivot = globalPivot;
            found = false;
            do {
                for (Iterator<AssembledRoute> it = routes.iterator(); it.hasNext(); ) {
                    AssembledRoute element = it.next();
                    if (element.getDestination().equals(pivot.getSource())) {
                        reordered.addFirst(element);
                        pivot = element;
                        it.remove();
                        found = true;
                        break;
                    }
                }
            } while (found);
            routes = reordered;
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
                if (candidate.getSourceAddress().equals(source)
                        && candidate.getDestinationAddress().equals(destination)) {
                    assembled.add(candidate);
                    originalRoutes.remove(i);
                    i--;
                }
            }
            assembled.add(sample);
            routes.add(new AssembledRoute(source, destination, assembled));
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
