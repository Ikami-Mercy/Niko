package com.myapplication.data;

import com.myapplication.constants.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit service to handle Network requests
 */
public interface ApiService {


    // Get location details
    @GET(Constants.MAPS_LOCATION_URL)
    Call<LocationResponse> getLocation(
            @Query("format") String format,
            @Query("lat") Double lat,
            @Query("lon") Double lon,
            @Query("zoom") int zoom,
            @Query("addressdetails") int addressDetails
    );
}
