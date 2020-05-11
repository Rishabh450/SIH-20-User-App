package com.example.userapp;

import android.app.Application;


import com.onesignal.OneSignal;

public class ConfigClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)

                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();


    }
}