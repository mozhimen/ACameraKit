package com.mozhimen.camerak.dahua.exam.common;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class EMFCMTokenRefreshService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMTokenRefreshService";
    private static String strRegisterId;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String registerId = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "onTokenRefresh: " + registerId);
        strRegisterId = registerId;
    }
}