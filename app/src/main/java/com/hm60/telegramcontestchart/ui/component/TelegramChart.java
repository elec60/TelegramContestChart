package com.hm60.telegramcontestchart.ui.component;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class TelegramChart extends View {

    private Paint[] paints;
    private Path[] paths;

    private List<Integer[]> yDataList;
    private Long[] xData;

    public TelegramChart(Context context) {
        super(context);

    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TelegramChart(Context context, @ Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




}
