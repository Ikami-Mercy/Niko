package com.myapplication.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.myapplication.Presenters.MapPresenter;
import com.myapplication.R;
import com.myapplication.data.LocationResponse;
import com.myapplication.maps.MapContract;
import com.myapplication.utils.Constants;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import timber.log.Timber;

public class MapActivity extends AppCompatActivity implements MapContract.View {
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private ProgressDialog progressBar;
    private ImageButton back;
    private CardView search;
    private TextView searchTv;
    private GpsMyLocationProvider geoprovider;
    private MapPresenter mapPresenter;
    private boolean backPressOkay = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double userLocationLat = 0.0;
    private Double userLocationLon = 0.0;
    private String userLocationName = "";
    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);

        back = findViewById(R.id.back);
        search = findViewById(R.id.searchCard);
        searchTv = findViewById(R.id.searchTextView);

        initializeMap();
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Fetching location details...");
        progressBar.setCancelable(false);

        mapPresenter = new MapPresenter(this);

        back.setOnClickListener(view -> super.onBackPressed());
        search.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, SearchLocationActivity.class), Constants.PICK_SEARCH_LOCATION);
        });
    }

    private void initializeMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        markers = new ArrayList<>();

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setClickable(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(Constants.NAIROBI_LAT, Constants.NAIROBI_LON);

        geoprovider = new GpsMyLocationProvider(this);
        geoprovider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(geoprovider, map);
        locationOverlay.enableFollowLocation();

        map.getController().setZoom(18D);
        map.getController().setCenter(startPoint);
//        map.getOverlayManager().add(locationOverlay);
        setCurrentLocation();

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                mapPresenter.getLocation(p.getLatitude(), p.getLongitude());
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);
    }

    private void setCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                Timber.e("Updating center point...");
                GeoPoint center = new GeoPoint(location.getLatitude(), location.getLongitude());
                map.getController().animateTo(center);

                Marker centerMarker = new Marker(map);
                centerMarker.setPosition(center);
                centerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                centerMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_map_marker));
                centerMarker.setOnMarkerClickListener((marker, mapView) -> {
                    mapPresenter.getLocation(location.getLatitude(), location.getLongitude());
                    return true;
                });

                markers.add(centerMarker);
                map.getOverlays().add(centerMarker);

                mapPresenter.getUserLocation(location.getLatitude(), location.getLongitude());
            } else {
                Timber.e("Last location is null");
            }
        });
    }

    @Override
    public void showLoading() {
        progressBar.show();
    }

    @Override
    public void onLocationFetched(LocationResponse response) {
        progressBar.dismiss();
        String location = response.getAddress().getSummary();
        Log.e(">>>>>>>>>>>>>>>",Constants.LOCATION_LAT+Constants.LOCATION_LON);
        if (response.getAddress().isValidCountry()) {




            new SweetAlertDialog(this, SweetAlertDialog.BUTTON_CONFIRM)
                    .setTitleText("Confirm Location")
                    .setContentText("Set " + location + " as your current location?")
                    .setCustomImage(ContextCompat.getDrawable(this, R.drawable.ic_add_location_black_24dp))
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.LOCATION_DETAILS, location);
                            intent.putExtra(Constants.LOCATION_LAT, Double.valueOf(response.getLat()));
                            intent.putExtra(Constants.LOCATION_LON, Double.valueOf(response.getLon()));
                            intent.putExtra(Constants.LOCATION_NAME, response.getAddress().getState());
                            intent.putExtra(Constants.LOCATION_LAT_USER, userLocationLat);
                            intent.putExtra(Constants.LOCATION_LON_USER, userLocationLon);
                            intent.putExtra(Constants.LOCATION_NAME_USER, userLocationName);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();

        } else {
            toast("Selected location is outside " + Constants.COUNTRY);
        }
    }

    @Override
    public void onUserLocationFetched(LocationResponse response) {
        if (response.getAddress().isValidCountry()) {
            userLocationName = response.getAddress().getState();
            userLocationLat = Double.valueOf(response.getLat());
            userLocationLon = Double.valueOf(response.getLon());
        }
    }

    @Override
    public void onLocationError() {
        progressBar.dismiss();
        toast("Error fetching location. Please try again");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_SEARCH_LOCATION && data != null) {
            double lat = data.getDoubleExtra(Constants.LOCATION_LAT, 0.0);
            double lon = data.getDoubleExtra(Constants.LOCATION_LON, 0.0);

            String search = data.getStringExtra(Constants.LOCATION_SEARCH);
            if (search != null && !search.isEmpty()) searchTv.setText(search);

            if (lat != 0.0 && lon != 0.0) {
                for (Marker marker : markers) {
                    map.getOverlays().remove(marker);
                }
                markers.clear();

                GeoPoint center = new GeoPoint(lat, lon);
                map.getController().animateTo(center);

                Marker centerMarker = new Marker(map);
                centerMarker.setPosition(center);
                centerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                centerMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location_marker_24dp));
                centerMarker.setOnMarkerClickListener((marker, mapView) -> {
                    mapPresenter.getLocation(lat, lon);
                    return true;

                });

                markers.add(centerMarker);
                map.getOverlays().add(centerMarker);
            }
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public void onResume() {
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onBackPressed() {
        if (backPressOkay) {
            super.onBackPressed();
        } else {
            toast("Tap back again to exit");
            backPressOkay = true;

            new Handler().postDelayed(() -> {
                backPressOkay = false;
            }, 1500);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mapPresenter.onDestroy();
    }
}
