package com.toyproject.honeyimleaving.retrofit.model;

/**
 * Created by iamfe on 2018-09-05.
 */

public class Candidates {
    String formatted_address;
    Candidates.Geometry geometry;
    String name;

    static class Geometry {
        Result.Geometry.LatLng location;
        Result.Geometry.Rect viewport;

        static class Rect {
            Candidates.Geometry.LatLng northeast, southwest;
        }

        static class LatLng {
            double lat, lng;
        }

        public Result.Geometry.Rect getViewport() {
            return viewport;
        }

        public double getLatitude() {
            return location.lat;
        }
        public double getLongitude() {
            return location.lng;
        }
    }

    public String getPlaceName() {
        return name;
    }

    public double getLatitude() {
        return  geometry.getLatitude();
    }
    public double getLongitude() {
        return  geometry.getLongitude();
    }

    public String getFormattedAddress() {
        return formatted_address;
    }

    @Override
    public String toString() {
        return name + "\n(" + formatted_address +")" ;
    }
}
