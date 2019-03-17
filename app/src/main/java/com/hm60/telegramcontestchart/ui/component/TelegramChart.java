package com.hm60.telegramcontestchart.ui.component;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class TelegramChart extends View {
    public TelegramChart(Context context) {
        super(context);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TelegramChart(Context context, @ Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private Paint[] paints;
    private Path[] paths;



}
