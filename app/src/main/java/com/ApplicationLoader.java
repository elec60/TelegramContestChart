package com;

import android.app.Application;

import com.hm60.telegramcontestchart.AndroidUtilities;

public class ApplicationLoader extends Application {

    private static ApplicationLoader INSTANCE;

    public ApplicationLoader() {
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        AndroidUtilities.init(this);
    }

    public static ApplicationLoader getInstance() {
        return INSTANCE;
    }
}
