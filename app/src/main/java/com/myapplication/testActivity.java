package com.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class testActivity extends AppCompatActivity implements LocationListener {
    private ImageView niko_btn;
    private EditText niko_et;
    private LocationManager locationManager;
    private static final int SELECT_LOCATION = 8;
    private ProgressDialog dialog;
    private FusedLocationProviderClient locationProviderClient;
    private String coordinates;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        niko_btn = findViewById(R.id.niko_btn);
        niko_et = findViewById(R.id.niko_et);
        niko_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLocation();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    public void onLocationChanged(Location location) {
      Log.e("LocationChanged " , "CALLED@#")
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    Log.e("StatusChanged " , s)
    }

    @Override
    public void onProviderEnabled(String s) {
     Log.e("ProviderEnabled " , s)
    }

    @Override
    public void onProviderDisabled(String s) {
     Log.e("ProviderDisabled " , s)
    }
    private void fetchLocation() {
        if (!checkLocationPermission())


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String coordinates = null;
                        try {
                            coordinates = "Longitude: " + (location.getLongitude()) + "  \nLatitude: " + String.valueOf(location.getLatitude());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        niko_et.setText(coordinates);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
    }
    private boolean checkLocationPermission() {
        final boolean[] granted = {false};

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                    .withContext(testActivity.this)
                                    .withTitle("Location permission")
                                    .withMessage("Location permissions are required to send location")
                                    .withButtonText(android.R.string.ok)
                                    .build();
                        } else if (report.areAllPermissionsGranted()) {

                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                buildAlertMessageNoGps();
                            } else {


                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }

                                dialog.show();
                                locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        dialog.setCancelable(false);

                                        if (location != null) {
//                                        dialog.dismiss();
                                            coordinates = null;
                                            try {
                                                coordinates = "Longitude: " + (location.getLongitude()) + "  \nLatitude: " + String.valueOf(location.getLatitude());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            // Pass the coordinates to the Map View.
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
//                                        dialog.dismiss();
                                        niko_et.setText(coordinates);
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        } else {

                            granted[0] = true;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return granted[0];
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialg, final int id) {
//                        dialog.setMessage("Getting co-ordinates, please wait.");
//                        dialog.show();
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SELECT_LOCATION);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(testActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
