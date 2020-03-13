package com.myapplication.data;


import com.myapplication.maps.RateLimitInterceptor;
import com.myapplication.utils.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


    /**
     * The method returns the OSM retrofit instance
     */
    public static ApiService getMapsClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RateLimitInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MAPS_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
