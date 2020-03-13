package com.myapplication.data;

import com.myapplication.maps.SearchLocation;
import com.myapplication.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    // Se
    // arch location details
    @GET(Constants.MAPS_SEARCH_URL)
    Call<List<SearchLocation>> searchLocation(
            @Query("format") String format,
            @Query("q") String location,
            @Query("addressdetails") int addressDetails
    );
}
