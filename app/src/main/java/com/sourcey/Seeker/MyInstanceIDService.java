package com.sourcey.Seeker;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        Login.token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM>>>>>>>>>>>>>","Token:"+Login.token);
    }

}
