package com.myapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.myapplication.callbacks.PermissionsListener;

public class LocationPermissionUtil {

    /**
     * Function to request Location permission
     */
    public static void requestLocationPermission(Context context, PermissionsListener listener) {
        Dexter.withActivity((Activity) context)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        listener.onPermissionRequest(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        listener.onPermissionRequest(false);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}
