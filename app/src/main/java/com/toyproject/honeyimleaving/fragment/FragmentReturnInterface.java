package com.toyproject.honeyimleaving.fragment;

/**
 * Created by iamfe on 2018-09-14.
 */

public interface FragmentReturnInterface<T> {

    public T getFragementReturn();
    public String getErrorString();
    public boolean isError();
}
