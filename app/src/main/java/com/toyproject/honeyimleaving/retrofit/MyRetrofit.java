package com.toyproject.honeyimleaving.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iamfe on 2018-08-28.
 */

public class MyRetrofit {
    private static MyRetrofit retrofitInstance = null;
    private Retrofit retrofit;
    private MyGoogleGeocodingAPI mMyGoogleAPIService;
    private MyGooglePlaceAPI mMyGooglePlaceAPI;

    private MyRetrofit() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(MyGoogleGeocodingAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 파싱등록
                .build();
    }

    public static MyRetrofit getInstance() {
        if(retrofitInstance == null)
            retrofitInstance = new MyRetrofit();

        return retrofitInstance;
    }

    public MyGoogleGeocodingAPI getServiceGoolgeGeoCoding() {
        this.mMyGoogleAPIService = this.retrofit.create(MyGoogleGeocodingAPI.class);
        return mMyGoogleAPIService;
    }

    public MyGooglePlaceAPI getSetviceGooglePlace() {
        this.mMyGooglePlaceAPI = this.retrofit.create(MyGooglePlaceAPI.class);
        return mMyGooglePlaceAPI;
    }
}
