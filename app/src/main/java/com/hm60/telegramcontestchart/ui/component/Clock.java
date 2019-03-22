package com.hm60.telegramcontestchart.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hm60.telegramcontestchart.AndroidUtilities;

public class Clock extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Clock(Context context) {
        super(context);

        init();
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setTextSize(dp(14));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(0, 0, 0);

        int width = getWidth();
        int height = getHeight();

        float radius = Math.min(height, width) / 2f - dp(10);

        paint.setStrokeWidth(dp(2));
        canvas.drawCircle(width / 2f, height / 2f, radius, paint);

        paint.setStrokeWidth(dp(1));

        canvas.translate(width / 2f, height / 2f);
        for (int i = 0; i < 60; i++) {
            canvas.save();
            canvas.rotate(6 * i);
            canvas.drawLine(0, -radius, 0, -radius + dp(20), paint);
            canvas.restore();

            canvas.save();
            canvas.rotate(90 + 6*i);
            canvas.drawText("" + (i + 1), -radius,0,paint);
            canvas.restore();
        }

    }



    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * value);
    }


}
