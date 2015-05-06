package com.teamd.taxi.service;

/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;*/

import com.google.maps.*;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistanceCalculator {

    private String apiKey;

    @Autowired
    public DistanceCalculator(String googleApiKey) {
        this.apiKey = apiKey;
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
}