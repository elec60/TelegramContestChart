package com.hm60.telegramcontestchart;

import android.content.Context;

public class AndroidUtilities {

    private static float density = 1;

    public static void init(Context context){
        density = context.getResources().getDisplayMetrics().density;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }
}
