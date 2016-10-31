package com.example.adriano.ihc.Controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.adriano.ihc.Activity.ActivityMain;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class Localizacao implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationListener locationListener;
    private String localAtual;
    private Context context;
    private String respota;

    public Localizacao(Context context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public String getLocalAtual() {
        return localAtual;
    }

    public boolean status() {
        return mGoogleApiClient.hasConnectedApi(LocationServices.API);
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    private void convEndCoord(String endereco) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            addresses = geocoder.getFromLocationName(endereco, 1);
            Address address = addresses.get(0);
            //latLng = new LatLng(address.getLatitude(), address.getLongitude());
            respota = "".concat(address.getLatitude() + "").concat(", ").concat(address.getLongitude() + "");
        } catch (Exception e) {
            Log.e("Teste", "Erro em Localizacao:" + e.getMessage());
        }
    }

    private void getCidade(String coordenadas) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            String vec[] = coordenadas.split(",");
            double x = Double.parseDouble(vec[0].trim()), y = Double.parseDouble(vec[1].trim());
            addresses = geocoder.getFromLocation(x, y, 1);
            Address address = addresses.get(0);
            respota = "" + address.getLocality();
        } catch (Exception e) {
            Log.e("Teste", "Erro em Localizacao:" + e.getMessage());
        }
    }

    private void convCoordEnd(String coordenadas) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            String vec[] = coordenadas.split(",");
            double x = Double.parseDouble(vec[0].trim()), y = Double.parseDouble(vec[1].trim());
            addresses = geocoder.getFromLocation(x, y, 1);
            Address address = addresses.get(0);
            respota = address.getThoroughfare();
        } catch (Exception e) {
            Log.e("Teste", "Erro em Localizacao:" + e.getMessage());
        }
//        Log.i("Teste","País:"+address.getCountryName());
//        Log.i("Teste","Cidade:"+address.getLocality());
//        Log.i("Teste","Cep:"+address.getPostalCode());
//        Log.i("Teste","Estado:"+address.getAdminArea());
//        Log.i("Teste","Número:"+address.getFeatureName());
//        Log.i("Teste","Bairro:"+address.getSubLocality());
//        Log.i("Teste","Endereço:"+address.getThoroughfare());

    }

    public String buscar(String... params) {
        if (params[0].equals("coordenadas")) {
            convEndCoord(params[1]);
        } else if (params[0].equals("cidade")) {
            getCidade(params[1]);
        } else if (params[0].equals("endereco")) {
            convCoordEnd(params[1]);
        }
        return respota;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000); //10 segundos -- intervalo ideal
            mLocationRequest.setFastestInterval(5000); //5 segundos -- intervalo mínimo
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    localAtual = location.getLatitude() + "," + location.getLongitude();
                }
            };

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }
    }

    private void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
        } catch (Exception e) {
            Log.e("Teste", "Erro em stopLocationUpdates() >>:" + e.getMessage());
        }
    }


}
