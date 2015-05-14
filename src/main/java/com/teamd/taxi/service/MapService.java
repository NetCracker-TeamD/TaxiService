package com.teamd.taxi.service;

import com.google.maps.*;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapService {

    private String apiKey;

    @Autowired
    public MapService(String googleApiKey) {
        this.apiKey = googleApiKey;
    }

    public Long calculateDistanceInMeters(String from, String to, String[] waypoints) throws Exception {
        GeoApiContext ctx = new GeoApiContext();
        ctx.setApiKey(apiKey);
        DirectionsApiRequest req = DirectionsApi.newRequest(ctx)
                .mode(TravelMode.DRIVING)
                .origin(from)
                .destination(to)
                .waypoints(waypoints);

        DirectionsRoute[] routes = req.await();
        if (routes.length > 0) {
            DirectionsRoute route = routes[0];
            Long distance = 0L;
            for (DirectionsLeg leg : route.legs) {
                distance += leg.distance.inMeters;
            }
            return distance;
        }
        return null;
    }

    public boolean isExists(String address) throws MapServiceNotAvailableException {
        try {
            calculateDistanceInMeters(address, address, null);
        } catch (NotFoundException notFound) {
            return false;
        } catch (Exception ex) {
            throw new MapServiceNotAvailableException(ex);
        }
        return true;
    }
}