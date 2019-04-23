package com.toyproject.honeyimleaving.retrofit;

import com.toyproject.honeyimleaving.retrofit.model.ResponsePlace;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iamfe on 2018-09-07.
 */

public interface MyGooglePlaceAPI {
    public static String LANGUAGE = "ko";
    public static String FIELDS = "formatted_address,name,geometry";
    public static String INPUTTYPE = "textquery";

    @GET("maps/api/place/findplacefromtext/json")
    Call<ResponsePlace> getPlaceFromAddress(@Query("input") String name,
                                            @Query("fields") String fields,
                                            @Query("inputtype") String inputType,
                                            @Query("language") String language,
                                            @Query("key") String key);
}
