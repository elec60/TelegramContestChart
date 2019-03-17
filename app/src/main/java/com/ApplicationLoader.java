package com;

import android.app.Application;

import com.hm60.telegramcontestchart.AndroidUtilities;

public class ApplicationLoader extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidUtilities.init(this);
    }
}
