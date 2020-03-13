package com.myapplication.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.myapplication.R;
import com.myapplication.constants.Constants;
import com.myapplication.maps.LocationResponse;
import com.myapplication.maps.MapActivity;
import com.myapplication.maps.MapContract;
import com.myapplication.maps.MapPresenter;
import com.myapplication.utils.GpsUtil;
import com.myapplication.utils.LocationPermissionUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Objects;

import static com.myapplication.constants.Constants.NAIROBI_LAT;
import static com.myapplication.constants.Constants.NAIROBI_LON;

public class LandingActivity extends AppCompatActivity implements MapContract.View{
    private ProgressDialog progressDialog;
    private ImageView niko_btn;
    private LocationManager locationManager;
    private EditText niko_et;
    private MapView map = null;
    private MapPresenter mapPresenter;
    private Double locationLongitude;
    private Double locationLatitude;
    private String locationName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);
        progressDialog = new ProgressDialog(this);
        locationManager = (LocationManager) Objects.requireNonNull(this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE));


        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(false);

        mapPresenter = new MapPresenter(this);

        IMapController mapController = map.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(NAIROBI_LAT, NAIROBI_LON);
        mapController.setCenter(startPoint);


        niko_et= findViewById(R.id.niko_et);
        niko_btn= findViewById(R.id.niko_btn);
        niko_btn.setOnClickListener(v->{

            fetchLocation();


        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_LOCATION && resultCode == RESULT_OK)
            progressDialog.dismiss();
        if (data == null) {
            progressDialog.dismiss();
            niko_et.setText("");
        } else {
            progressDialog.dismiss();

            setLocationLatitude(data.getDoubleExtra(Constants.LOCATION_LAT, 0.0));
            setLocationLongitude(data.getDoubleExtra(Constants.LOCATION_LON, 0.0));

            locationName = data.getStringExtra(Constants.LOCATION_DETAILS);
            niko_et.setText(locationName);

            GeoPoint center = new GeoPoint(getLocationLatitude(), getLocationLongitude());
            map.getController().animateTo(center);

            Marker centerMarker = new Marker(map);
            centerMarker.setPosition(center);
            centerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            centerMarker.setIcon(ContextCompat.getDrawable(Objects.requireNonNull(this), R.drawable.ic_map_marker));
            centerMarker.setOnMarkerClickListener((marker, mapView) -> {
                mapPresenter.getLocation(getLocationLatitude(), getLocationLongitude());
                return true;
            });

            //markers.add(centerMarker);
            map.getOverlays().add(centerMarker);
        }
    }


    //-- Location


    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    private void fetchLocation() {

        if (!checkLocationPermission()) return;

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Intent intent = new Intent(LandingActivity.this.getApplicationContext(), MapActivity.class);
            startActivityForResult(intent, Constants.PICK_LOCATION);
        }
    }

    private boolean checkLocationPermission() {
        final boolean[] granted = {false};

        Dexter.withActivity(LandingActivity.this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                    .withContext(LandingActivity.this)
                                    .withTitle("Location permission")
                                    .withMessage("Location permissions are required to send location")
                                    .withButtonText(android.R.string.ok)
                                    .build();
                        } else if (report.areAllPermissionsGranted()) {

                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                buildAlertMessageNoGps();
                            } else {
                                progressDialog.show();

                                if (ActivityCompat.checkSelfPermission(LandingActivity.this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LandingActivity.this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }

//                                dialog.show();
                                Intent intent = new Intent(LandingActivity.this, MapActivity.class);
                                startActivityForResult(intent, Constants.PICK_LOCATION);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialg, final int id) {

                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.PICK_LOCATION);

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

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
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
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(this).getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void onLocationFetched(LocationResponse response) {

    }

    @Override
    public void onUserLocationFetched(LocationResponse response) {

    }

    @Override
    public void onLocationError() {

    }
}
