package com.toyproject.honeyimleaving.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ResponseRevGeoCoding {
    @SerializedName("results")  List<Result> results;
    @SerializedName("status")  String status;

    public List<Result> getResults() {
        return results;
    }

}
