package com.myapplication.maps;

public interface MapContract {


    interface View {
        void showLoading();

        void onLocationFetched(LocationResponse response);

        void onUserLocationFetched(LocationResponse response);

        void onLocationError();
    }

    interface Presenter {
        void getLocation(Double lat, Double lon);

        void getUserLocation(Double lat, Double lon);

        void onDestroy();
    }
}
