package com.hm60.telegramcontestchart.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hm60.telegramcontestchart.AndroidUtilities;

import java.util.List;

public class TelegramChart extends View {

    private Paint[] paints;
    private Path[] paths;
    private Paint[] paintsSmall;
    private Path[] pathsSmall;


    private List<Integer[]> yDataListOriginal;
    private Long[] xData;
    private String[] names;
    private String[] colors;
    private String[] types;

    private Paint smallSectionBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF leftRect;

    public TelegramChart(Context context) {
        super(context);

        smallSectionBackgroundPaint.setStyle(Paint.Style.FILL);
        smallSectionBackgroundPaint.setColor(Color.parseColor("3BBDB9B9"));

    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(List<Integer[]> yDataList, Long[] xData, String[] names, String[] colors, String[] types) {
        this.yDataListOriginal = yDataList;
        this.xData = xData;
        this.names = names;
        this.colors = colors;
        this.types = types;

        paints = new Paint[yDataList.size()];
        for (int i = 0; i < paints.length; i++) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.parseColor(colors[i]));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(AndroidUtilities.dp(2));
            paints[i] = p;
        }

        paths = new Path[yDataList.size()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = new Path();
        }

        paintsSmall = new Paint[yDataList.size()];
        for (int i = 0; i < paintsSmall.length; i++) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.parseColor(colors[i]));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(AndroidUtilities.dp(1.5f));
            paintsSmall[i] = p;
        }

        pathsSmall = new Path[yDataList.size()];
        for (int i = 0; i < pathsSmall.length; i++) {
            pathsSmall[i] = new Path();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawSmallSection(canvas);


    }

    private void drawSmallSection(Canvas canvas) {

        float height = getHeight() / 7f;

        canvas.drawLine(0, 0, getWidth(), getHeight(), paints[0]);

        canvas.save();

        canvas.clipRect(getWidth() / 2 - 50, getHeight() / 2 - 50, getWidth() / 2 + 50, getHeight() / 2 + 50);

        canvas.drawRect(0, 0, getWidth(), getHeight(), smallSectionBackgroundPaint);

        canvas.restore();

        //canvas.drawRect(getWidth() / 2f - 20, getHeight() / 2f - 20, getWidth() / 2f + 20, getHeight() / 2f + 20, smallSectionBackgroundPaint);


    }
}
