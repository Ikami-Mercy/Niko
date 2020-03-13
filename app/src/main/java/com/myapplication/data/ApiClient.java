package com.myapplication.data;

import com.myapplication.constants.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ApiClient {

    /**
     * The method returns the OSM retrofit instance
     */
    public static ApiService getMapsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MAPS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
