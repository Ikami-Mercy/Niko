package com.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

public class GpsUtil {

        private Context context;
        private SettingsClient mSettingsClient;
        private LocationSettingsRequest mLocationSettingsRequest;
        private LocationManager locationManager;
        private LocationRequest locationRequest;

        public GpsUtil(Context context) {
            this.context = context;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            mSettingsClient = LocationServices.getSettingsClient(context);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(2 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            mLocationSettingsRequest = builder.build();
            builder.setAlwaysShow(true); //this is the key ingredient
        }

        // method for turn on GPS
        public void turnGPSOn(onGpsListener onGpsListener) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (onGpsListener != null) {
                    onGpsListener.gpsStatus(true);
                }
            } else {
                mSettingsClient
                        .checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener((Activity) context, locationSettingsResponse -> {
                            if (onGpsListener != null) {
                                onGpsListener.gpsStatus(true);
                            }
                        })
                        .addOnFailureListener((Activity) context, e -> {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) context, 54566);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e("","PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e("Location error: %s", errorMessage);
                            }
                        });
            }
        }

        public interface onGpsListener {
            void gpsStatus(boolean isGPSEnable);
        }

    }
