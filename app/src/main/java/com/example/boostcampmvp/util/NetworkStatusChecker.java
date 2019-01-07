package com.example.boostcampmvp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusChecker {

    private static ConnectivityManager connectivityManager = null;

    public static boolean isNetworkConnected(Context context) {

        // 연결 확인 객체가 없는 경우에만 객체를 작성
        if(connectivityManager == null)
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
