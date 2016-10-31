package com.example.adriano.ihc.Controller;

import android.location.Location;
import android.util.Log;

public class CalcCoordenadas {

    private static double teste2(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        return locationA.distanceTo(locationB);
    }

    private static float teste(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[0];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'M') {
            dist = (dist * 1.609344) * 1000;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    //This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //this function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double distancia(String loc1, String loc2) {
        double lat1 = 0, lat2 = 0, long1 = 0, long2 = 0;
        try {
            String coordenadas1[] = loc1.split(","), coordenadas2[] = loc2.split(",");

            lat1 = Double.parseDouble(coordenadas1[0]);
            long1 = Double.parseDouble(coordenadas1[1]);
            lat2 = Double.parseDouble(coordenadas2[0]);
            long2 = Double.parseDouble(coordenadas2[1]);
            return teste2(lat1, long1, lat2, long2);
        } catch (Exception e) {
            Log.e("Teste", "erro na classe CalcCoordenadas em distancia() >>:" + e.getMessage());
        }
        return 0;
    }


}
