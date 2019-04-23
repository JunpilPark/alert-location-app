package com.toyproject.honeyimleaving.fragment;

/**
 * Created by iamfe on 2018-09-14.
 */

public class FragmentReturnErrCheckUtil {

    public static String getErrorFragment(FragmentReturnInterface fragmentReturnInterface) {
        return fragmentReturnInterface.getErrorString();
    }

    public static boolean isErrorFragment(FragmentReturnInterface fragmentReturnInterface) {
        return fragmentReturnInterface.isError();
    }
}
