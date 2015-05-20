package com.teamd.taxi.models;

import java.util.HashMap;

/**
 * Created by Олег on 19.05.2015.
 */
public class MapResponse extends HashMap<String, Object> {
    public MapResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}