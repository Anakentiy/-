package com.example.prv;

import android.app.Application;
import android.content.Context;

public class PRVApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        PRVApp.context = getApplicationContext();
    }

    public static Context getContext() {
        return PRVApp.context;
    }
}