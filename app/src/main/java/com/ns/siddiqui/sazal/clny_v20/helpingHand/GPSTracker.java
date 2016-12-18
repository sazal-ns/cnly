/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20.helpingHand;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by sazal on 2016-12-01.
 */

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    private boolean isNetworkEnable = false;
    private boolean isGPSEnable = false;
    private boolean canGetLocation = false;

    Location location = null;

    private double lat, lon;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private static final long MIN_TIME_BW_UPDATE = 1000 * 60;

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnable && !isGPSEnable) {

            } else {
                this.canGetLocation = true;
                if (isNetworkEnable) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATE,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE, this);
                    Log.d("****Network****","Network Enable");

                    if (locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            lat= location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnable){
                    if (location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATE,
                                MIN_DISTANCE_CHANGE_FOR_UPDATE,this);
                        Log.d("****GPS****","GPS Enable");
                        if (location != null){
                            lat= location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){
        if (location != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLat(){
        if (location != null)
         lat= location.getLatitude();
        return lat;
    }

    public double getLon(){
        if (location != null)
            lon = location.getLongitude();
        return lon;
    }

    public boolean canGetLocatin(){
        return this.canGetLocation;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
