package com.toyproject.honeyimleaving.retrofit;

import com.toyproject.honeyimleaving.retrofit.model.ResponseRevGeoCoding;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iamfe on 2018-08-28.
 */

public interface MyGoogleGeocodingAPI {
    public static String BASE_URL = "https://maps.googleapis.com/";

    @GET("maps/api/geocode/json")
    Call<ResponseRevGeoCoding> getGeoCodeLocationFromAddress(@Query("address") String address, @Query("KEY") String key, @Query("language") String language);

}
