package com.toyproject.honeyimleaving.retrofit.model;

import java.util.List;

/**
 * Created by iamfe on 2018-08-31.
 */

public class Result {
    String formatted_address;
    Geometry geometry;
    String place_id;
    List<String> types;

    static class Geometry {
        Rect bounds;
        LatLng location;
        String location_type;
        Rect viewport;

        static class Rect {
            LatLng northeast, southwest;
        }

        static class LatLng {
            double lat, lng;
        }

        public Rect getBounds() {
            return bounds;
        }

        public LatLng getLocation() {
            return location;
        }

        public String getLocation_type() {
            return location_type;
        }

        public Rect getViewport() {
            return viewport;
        }
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public Geometry.LatLng getGeometryLocation() {
        return geometry.getLocation();
    }

    public String getPlace_id() {
        return place_id;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return formatted_address;
    }
}
