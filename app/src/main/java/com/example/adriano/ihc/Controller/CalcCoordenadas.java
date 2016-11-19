package com.example.adriano.ihc.Controller;

import android.location.Location;
import android.util.Log;

public class CalcCoordenadas {

    private static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        return locationA.distanceTo(locationB);
    }

    public static double distancia(String loc1, String loc2) {
        double lat1 = 0, lat2 = 0, long1 = 0, long2 = 0;
        try {
            String coordenadas1[] = loc1.split(","), coordenadas2[] = loc2.split(",");

            lat1 = Double.parseDouble(coordenadas1[0]);
            long1 = Double.parseDouble(coordenadas1[1]);
            lat2 = Double.parseDouble(coordenadas2[0]);
            long2 = Double.parseDouble(coordenadas2[1]);
            return calcularDistancia(lat1, long1, lat2, long2);
        } catch (Exception e) {
            Log.e("Teste", "erro na classe CalcCoordenadas em distancia() >>:" + e.getMessage());
        }
        return 0;
    }


}
