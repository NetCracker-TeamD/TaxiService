package com.teamd.taxi.service;

import com.google.maps.*;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MapService {

    private String apiKey;

    @Autowired
    public MapService(String googleApiKey) {
        this.apiKey = googleApiKey;
    }

    public Float calculateDistanceInKilometers(String from, String to) throws NotFoundException, MapServiceNotAvailableException {
        System.out.println("count from[" + from + "] to [" + to + "]");
        try {
            GeoApiContext ctx = new GeoApiContext();
            ctx.setApiKey(apiKey);
            DirectionsApiRequest req = DirectionsApi.newRequest(ctx)
                    .mode(TravelMode.DRIVING)
                    .origin(from)
                    .destination(to);

            DirectionsRoute[] routes = req.await();
            if (routes.length > 0) {
                DirectionsRoute route = routes[0];
                long distance = 0L;
                for (DirectionsLeg leg : route.legs) {
                    distance += leg.distance.inMeters;
                }
                float retVal = (float) (distance / 1000.0);
                return retVal;
            }
            return null;
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (Exception e) {
            throw new MapServiceNotAvailableException(e);
        }
    }

    public void checkAddress(String address) throws NotFoundException, MapServiceNotAvailableException {
        try {
            calculateDistanceInKilometers(address, address);
        } catch (NotFoundException notFound) {
            throw notFound;
        } catch (Exception ex) {
            throw new MapServiceNotAvailableException(ex);
        }
    }
}