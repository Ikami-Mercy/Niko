package com.myapplication.maps;
import com.myapplication.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MapPresenter implements MapContract.Presenter {
    private MapContract.View mapView;
    private ApiService apiService;

    public MapPresenter(MapContract.View mapView) {
        this.mapView = mapView;
        this.apiService = ApiClient.getMapsClient();
    }

    @Override
    public void getLocation(Double lat, Double lon) {
        mapView.showLoading();

        Timber.e("Fetching location details for: %s, %s", lat, lon);
        apiService.getLocation(Constants.MAPS_REQUEST_FORMAT, lat, lon, Constants.MAPS_REQUEST_ZOOM, Constants.MAPS_REQUST_ADDRESS_DETAILS)
                .enqueue(new Callback<LocationResponse>() {
                    @Override
                    public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                        if (mapView != null) {
                            if (response.body() != null && response.body().getError() == null) {
                                mapView.onLocationFetched(response.body());
                            } else {
                                Timber.e("Error fetching location");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationResponse> call, Throwable t) {
                        Timber.e("Error fetching location details: %s", t.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void getUserLocation(Double lat, Double lon) {
        Timber.e("Fetching user location details for: %s, %s", lat, lon);
        apiService.getLocation(Constants.MAPS_REQUEST_FORMAT, lat, lon, Constants.MAPS_REQUEST_ZOOM, Constants.MAPS_REQUST_ADDRESS_DETAILS)
                .enqueue(new Callback<LocationResponse>() {
                    @Override
                    public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                        if (mapView != null) {
                            if (response.body() != null && response.body().getError() == null) {
                                mapView.onUserLocationFetched(response.body());
                            } else {
                                mapView.onLocationError();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationResponse> call, Throwable t) {
                        Timber.e("Error fetching location details: %s", t.getLocalizedMessage());
                        if (mapView != null) mapView.onLocationError();
                    }
                });
    }

    @Override
    public void onDestroy() {
        mapView = null;
    }
}
