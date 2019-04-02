package com.hm60.telegramcontestchart.util;

import android.graphics.Color;

public final class ColorUtil {
    public static int combineColors(int color1, int color2, float color1Factor) {
        float color2Factor = 1.0f - color1Factor;
        return Color.argb((int) ((((float) Color.alpha(color1)) * color1Factor) + (((float) Color.alpha(color2)) * color2Factor)), (int) ((((float) Color.red(color1)) * color1Factor) + (((float) Color.red(color2)) * color2Factor)), (int) ((((float) Color.green(color1)) * color1Factor) + (((float) Color.green(color2)) * color2Factor)), (int) ((((float) Color.blue(color1)) * color1Factor) + (((float) Color.blue(color2)) * color2Factor)));
    }
}
