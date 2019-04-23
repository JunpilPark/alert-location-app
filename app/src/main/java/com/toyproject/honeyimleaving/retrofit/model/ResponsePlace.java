package com.toyproject.honeyimleaving.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by iamfe on 2018-09-05.
 */

public class ResponsePlace {
    @SerializedName("candidates")    List<Candidates> candidates;
    @SerializedName("status")  String status;

    public List<Candidates> getCandidates() {
        return candidates;
    }
}
