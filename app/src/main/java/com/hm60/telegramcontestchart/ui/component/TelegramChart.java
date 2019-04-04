package com.hm60.telegramcontestchart.ui.component;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hm60.telegramcontestchart.AndroidUtilities;
import com.hm60.telegramcontestchart.R;
import com.hm60.telegramcontestchart.util.ColorUtil;
import com.hm60.telegramcontestchart.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TelegramChart extends View {

    public static final int chartColorDark = 0xff1d2733;
    public static final int chartColorLight = 0xffffffff;
    private int color = chartColorLight;


    private static final int gridLineColorLight = 0xfff3f3f3;
    private static final int gridLineColorDark = 0xff182122;

    private static final int gridTextColorLight = 0xffa0abb2;
    private static final int gridTextColorDark = 0xff4d5f6e;

    private static final int selectedCircleFillColorLight = 0xffffffff;
    private static final int selectedCircleFillColorDark = 0xff1d2733;

    private static final int infoBoxColorLight = 0xffffffff;
    private static final int infoBoxColorDark = 0xff202b38;

    private ChartData chartData;

    private float ratio = 11f;
    private float slidingRectRatio = 0.25f;// 0.25 of progress section width
    private float slidingRectMinWith = AndroidUtilities.dp(20);

    private Paint gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean showInfoBox;
    private float InfoBoxX;
    private Path[] infoBoxPaths;
    private Paint indicatorCirclePaint = new Paint();
    private Paint indicatorLinePaint = new Paint();
    private Path indicatorLinePath = new Path();
    private RectF tooltipRect = new RectF();
    private Paint tooltipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private Paint[] paints;
    private Path[] paths;
    private Paint[] paintsSmall;
    private Path[] pathsSmall;
    private TextPaint textPaint = new TextPaint();

    private RectF slidingRect = new RectF();
    private RectF smallForegroundRect = new RectF();

    private boolean isInit;

    private Paint smallForegroundPaint = new Paint();
    private Paint handlesPaint = new Paint();

    private boolean regenerate = true;

    private RectF leftHandle = new RectF();
    private RectF rightHandle = new RectF();

    private float lastMaxValue;

    private boolean isDraggingLeftHandle;
    private boolean isDraggingRightHandle;
    private boolean isDraggingSlidingRect;
    private float lastXLeft;
    private float lastXRight;
    private float lastXSlidingRect;


    public TelegramChart(Context context) {
        super(context);

        init();
    }

    public TelegramChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TelegramChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private static float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    private void init() {
        chartData = new ChartData();

        smallForegroundPaint.setStyle(Paint.Style.FILL);
        smallForegroundPaint.setColor(getResources().getColor(R.color.blurColor));

        handlesPaint.setStyle(Paint.Style.FILL);
        handlesPaint.setColor(0x5ED3CACA);

        selectedCirclePaint.setStyle(Paint.Style.FILL);
        selectedCirclePaint.setColor(0x88ECECF5);

        gridLinePaint.setColor(getResources().getColor(R.color.xAxisColor));
        gridLinePaint.setStyle(Paint.Style.STROKE);

        textPaint.setColor(getResources().getColor(R.color.textColor));
        textPaint.setTextSize(AndroidUtilities.dp(10));

        indicatorCirclePaint.setColor(getResources().getColor(R.color.chartBackgroundColor));
        indicatorCirclePaint.setStyle(Paint.Style.FILL);

        indicatorLinePaint.setColor(getResources().getColor(R.color.indicatorLineColor));
        indicatorLinePaint.setStyle(Paint.Style.STROKE);
        indicatorLinePaint.setStrokeWidth(AndroidUtilities.dp(1));

        tooltipPaint.setStyle(Paint.Style.FILL);
        tooltipPaint.setColor(getResources().getColor(R.color.tooltipBackColor));
        tooltipPaint.setShadowLayer(5f, 0f, 2f, getResources().getColor(R.color.shadowColor));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    public int getBackgroundColor() {
        return this.color;
    }

    public void applyTheme(float colorLightFactor) {
        this.color = ColorUtil.combineColors(chartColorLight, chartColorDark, colorLightFactor);
        invalidate();
    }

    public void setData(List<Integer[]> yDataList, long[] xData, String[] names, String[] colors, String[] types, String title) {
        chartData.title = title;
        chartData.yDataOriginal = yDataList;
        chartData.xDataOriginal = xData;
        chartData.names = names;

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
            p.setStrokeWidth(AndroidUtilities.dp(1.2f));
            paintsSmall[i] = p;
        }

        pathsSmall = new Path[yDataList.size()];
        for (int i = 0; i < pathsSmall.length; i++) {
            pathsSmall[i] = new Path();
        }

        infoBoxPaths = new Path[yDataList.size()];
        for (int i = 0; i < infoBoxPaths.length; i++) {
            infoBoxPaths[i] = new Path();
        }

        int maxValue = Integer.MIN_VALUE;
        for (Integer[] ys : yDataList) {
            for (Integer y : ys) {
                if (y > maxValue) maxValue = y;
            }
        }

        lastMaxValue = maxValue;

        chartData.yDataNormalized = new ArrayList<>(yDataList.size());
        for (Integer[] ys : yDataList) {
            Float[] yNormalized = new Float[ys.length];
            for (int i = 0; i < ys.length; i++) {
                float f = (float) (ys[i]) / maxValue;
                yNormalized[i] = f;
            }
            chartData.yDataNormalized.add(yNormalized);
        }

        chartData.xs = new float[xData.length];
        chartData.visibles = new boolean[yDataList.size()];
        for (int i = 0; i < chartData.visibles.length; i++) {
            chartData.visibles[i] = true;
        }


        chartData.labels = new Label[xData.length];
        for (int i = 0; i < xData.length; i++) {
            Label label = new Label(Float.MAX_VALUE, 0, Utils.toShortDateString(xData[i]));
            chartData.labels[i] = label;
        }

        regenerate = true;

        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(color);

        drawSmallSection(canvas);

        drawLargeSection(canvas);
    }


    private void drawSmallSection(Canvas canvas) {

        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();

        float height = (getHeight() - paddingBottom - paddingTop) / ratio;
        float top = getHeight() - height - paddingBottom;
        float left = AndroidUtilities.dp(16);
        float bottom = top + height;
        float right = getWidth() - AndroidUtilities.dp(16);
        float width = right - left;

        if (!isInit) {
            slidingRect.top = top;
            slidingRect.bottom = bottom;
            slidingRect.right = right;
            slidingRect.left = slidingRect.right - width * slidingRectRatio;

            leftHandle.left = slidingRect.left;
            leftHandle.top = top + AndroidUtilities.dp(1);
            leftHandle.bottom = bottom - AndroidUtilities.dp(1);
            leftHandle.right = leftHandle.left + AndroidUtilities.dp(5);

            rightHandle.right = slidingRect.right;
            rightHandle.top = top + AndroidUtilities.dp(1);
            rightHandle.bottom = bottom - AndroidUtilities.dp(1);
            rightHandle.left = rightHandle.right - AndroidUtilities.dp(5);

            isInit = true;
        }

        if (regenerate) {
            regenerate = false;

            smallForegroundRect.left = left;
            smallForegroundRect.top = top;
            smallForegroundRect.right = right;
            smallForegroundRect.bottom = bottom;

            for (Path path : pathsSmall) {
                path.reset();
            }

            for (int i = 0; i < chartData.yDataNormalized.size(); i++) {
                boolean visible = chartData.visibles[i];
                if (visible) {
                    Float[] yn = chartData.yDataNormalized.get(i);

                    Path path = pathsSmall[i];
                    path.moveTo(left, top + (1 - yn[0]) * height);

                    for (int i1 = 1; i1 < yn.length; i1++) {

                        float y = top + (1 - yn[i1]) * height;
                        float x = left + (float) i1 * width / (yn.length - 1);

                        path.lineTo(x, y);
                    }
                }

            }
        }


        for (int i = 0; i < pathsSmall.length; i++) {
            boolean visible = chartData.visibles[i];
            if (visible) {
                canvas.drawPath(pathsSmall[i], paintsSmall[i]);
            }

        }

        //top border
        canvas.drawRect(slidingRect.left,
                slidingRect.top,
                slidingRect.right,
                slidingRect.top + AndroidUtilities.dp(1),
                handlesPaint);

        //bottom border
        canvas.drawRect(slidingRect.left,
                slidingRect.bottom - AndroidUtilities.dp(1),
                slidingRect.right,
                slidingRect.bottom,
                handlesPaint);

        canvas.drawRect(leftHandle, handlesPaint);
        canvas.drawRect(rightHandle, handlesPaint);

        canvas.save();
        canvas.clipRect(slidingRect, Region.Op.DIFFERENCE);

        canvas.drawRect(smallForegroundRect, smallForegroundPaint);

        canvas.restore();

    }


    private void drawLargeSection(Canvas canvas) {
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();


        float smallSectionHeight = (getHeight() - paddingBottom - paddingTop) / ratio;
        float height = getHeight() - paddingBottom - paddingTop - smallSectionHeight;
        float top = paddingTop;
        float bottom = top + height;
        float left = AndroidUtilities.dp(16);
        float right = getWidth() - AndroidUtilities.dp(16);
        float width = right - left;

        for (Label xLabel : chartData.labels) {
            xLabel.y = bottom - AndroidUtilities.dp(25);
        }

        //xAxis
        gridLinePaint.setStrokeWidth(0);
        gridLinePaint.setColor(getResources().getColor(R.color.xAxisColor));
        canvas.drawLine(left,
                bottom - AndroidUtilities.dp(40),
                right,
                bottom - AndroidUtilities.dp(40),
                gridLinePaint);

        //background lines
        gridLinePaint.setColor(getResources().getColor(R.color.backgroundLineColor));

        for (int i = 1; i <= 5; i++) {
            int y = (int) (bottom - smallSectionHeight - height / 6 * i);

            canvas.drawLine(left,
                    y,
                    right,
                    y,
                    gridLinePaint);
        }


        for (Path path : paths) {
            path.reset();
        }

        float w0 = smallForegroundRect.width();
        float w1 = width;
        float win = slidingRect.width();
        float T = w0 * w1 / win;
        float offsetX = (smallForegroundRect.right - slidingRect.right) * w1 / win;

        float xStep = (right - left - w1 + T) / (chartData.xDataOriginal.length - 1);
        chartData.xStep = xStep;
        chartData.resetLabelsX();

        for (int i = 0; i < chartData.yDataNormalized.size(); i++) {

            boolean visible = chartData.visibles[i];
            if (!visible) {
                continue;
            }
            Float[] yn = chartData.yDataNormalized.get(i);
            Path path = paths[i];

            for (int i1 = 0; i1 < yn.length; i1++) {

                float x = -T + w1 + left + i1 * xStep + offsetX;
                chartData.labels[i1].x = x;

                if (i1 == 0) {
                    float y = top + (1 - yn[i1]) * (height - AndroidUtilities.dp(40));
                    path.moveTo(x, y);
                }

                if (x < -xStep) {
                    continue;
                }

                if (x > getWidth() + xStep) {
                    break;
                }

                float y = top + (1 - yn[i1]) * (height - AndroidUtilities.dp(40));

                chartData.xs[i1] = x;


                path.lineTo(x, y);

            }

        }


        for (int i = 0; i < paths.length; i++) {
            boolean visible = chartData.visibles[i];
            if (visible) {
                canvas.drawPath(paths[i], paints[i]);
            }
        }

        //draw xLabels
        float r = w1 / T;
        int N = (int) (chartData.xDataOriginal.length * r);
        int iStep = N / 6;
        textPaint.setColor(getResources().getColor(R.color.textColor));
        textPaint.setTextSize(AndroidUtilities.dp(10));

        textPaint.setColor(Color.LTGRAY);
        for (int i = 0; i < chartData.labels.length; i += iStep) {
            Label label = chartData.labels[i];

            if ((label.x < -chartData.xStep)) {
                continue;
            }
            if ((label.x > getWidth() + chartData.xStep)) {
                break;
            }

            float w = textPaint.measureText(label.text);

            canvas.drawText(label.text, label.x - w, label.y, textPaint);
        }

        // TODO: 3/24/19 Tooltip size should be dynamic based on data
        if (showInfoBox) {
            if (chartData.yDataOriginal.size() == 4) {
                Toast.makeText(getContext(), "Unfortunately i was busy, so couldn't create dynamic Tooltip size and texts positions, but im able to create better one, animations have same condition:)", Toast.LENGTH_LONG).show();
            }
            indicatorLinePath.reset();

            for (Path tooltipPath : infoBoxPaths) {
                tooltipPath.reset();
            }
            int index = Math.round((InfoBoxX + T - w1 - left - offsetX) / xStep);
            if (index < 0) index = 0;
            if (index >= chartData.xDataOriginal.length) index = chartData.xDataOriginal.length - 1;
            float x = -T + w1 + left + index * xStep + offsetX;

            indicatorLinePath.moveTo(x, bottom - AndroidUtilities.dp(40));

            for (int i = 0; i < chartData.yDataOriginal.size(); i++) {
                boolean visible = chartData.visibles[i];
                if (visible) {
                    float y = top + (1 - chartData.yDataNormalized.get(i)[index]) * (height - AndroidUtilities.dp(40));
                    Path tooltipPath = infoBoxPaths[i];
                    tooltipPath.addCircle(x, y, AndroidUtilities.dp(4), Path.Direction.CW);
                }
            }

            indicatorLinePath.lineTo(x, paddingTop);

            for (int i = 0; i < infoBoxPaths.length; i++) {

                Path tooltipPath = infoBoxPaths[i];

                canvas.drawPath(tooltipPath, indicatorCirclePaint);
                canvas.drawPath(tooltipPath, paints[i]);
            }

            canvas.drawPath(indicatorLinePath, indicatorLinePaint);

            for (int i = 0; i < infoBoxPaths.length; i++) {
                Path tooltipPath = infoBoxPaths[i];
                canvas.drawPath(tooltipPath, indicatorCirclePaint);
                canvas.drawPath(tooltipPath, paints[i]);
            }

            tooltipRect.left = x - AndroidUtilities.dp(14);
            tooltipRect.top = top;
            tooltipRect.right = tooltipRect.left + AndroidUtilities.dp(84);
            tooltipRect.bottom = tooltipRect.top + AndroidUtilities.dp(54);

            float w = tooltipRect.width();
            if (tooltipRect.right > getWidth()) {
                tooltipRect.right = x + AndroidUtilities.dp(8);
                tooltipRect.left = tooltipRect.right - w;
            }


            canvas.drawRoundRect(tooltipRect, AndroidUtilities.dp(5), AndroidUtilities.dp(5), tooltipPaint);

            long millis = chartData.xDataOriginal[index];
            String dateOnTooltip = Utils.toShortDateString2(millis);
            textPaint.measureText(dateOnTooltip);
            float h = textPaint.descent() - textPaint.ascent();

            textPaint.setColor(getResources().getColor(R.color.tooltipTitleTextColor));
            textPaint.setTextSize(AndroidUtilities.dp(12));
            float titleTextY = top + h + AndroidUtilities.dp(2);
            float titleTextX = tooltipRect.left + AndroidUtilities.dp(8);

            canvas.drawText(dateOnTooltip,
                    titleTextX,
                    titleTextY,
                    textPaint);

            int n = 0;
            for (int i = 0; i < chartData.yDataOriginal.size(); i++) {
                boolean visible = chartData.visibles[i];

                if (visible) {
                    int count = chartData.yDataOriginal.get(i)[index];
                    String name = chartData.names[i];

                    textPaint.setColor(paints[i].getColor());
                    textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    canvas.drawText(String.valueOf(count),
                            titleTextX + n % 2 * AndroidUtilities.dp(42),
                            titleTextY + (float) Math.ceil((n + 1) / 2f) * AndroidUtilities.dp(20),
                            textPaint);

                    textPaint.setTypeface(null);
                    canvas.drawText(name,
                            titleTextX + n % 2 * AndroidUtilities.dp(42),
                            titleTextY + (float) Math.ceil((n + 1) / 2f) * AndroidUtilities.dp(32),
                            textPaint);

                    n++;
                }
            }


        }

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                float threshold = AndroidUtilities.dp(12);

                if (y >= leftHandle.top && y <= leftHandle.bottom &&
                        Math.abs(x - leftHandle.centerX()) <= threshold) {
                    isDraggingLeftHandle = true;
                    isDraggingRightHandle = false;
                    isDraggingSlidingRect = false;
                    lastXLeft = x;
                    getParent().requestDisallowInterceptTouchEvent(true);

                    return true;
                }

                if (y >= rightHandle.top && y <= rightHandle.bottom &&
                        Math.abs(x - rightHandle.centerX()) <= threshold) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = true;
                    isDraggingSlidingRect = false;
                    lastXRight = x;
                    getParent().requestDisallowInterceptTouchEvent(true);

                    return true;
                }

                if (slidingRect.contains(x, y)) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = false;
                    isDraggingSlidingRect = true;
                    lastXSlidingRect = x;
                    getParent().requestDisallowInterceptTouchEvent(true);

                    return true;
                }

                showInfoBox = true;
                InfoBoxX = x;

                invalidate();

                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();

                if (isDraggingLeftHandle) {
                    dragMode = DragMode.LeftHandle;
                    calcSlidingBound(x - lastXLeft);
                    invalidate();
                    lastXLeft = x;
                    return true;
                }

                if (isDraggingRightHandle) {
                    dragMode = DragMode.RightHandle;
                    calcSlidingBound(x - lastXRight);
                    invalidate();
                    lastXRight = x;
                    return true;
                }

                if (isDraggingSlidingRect) {
                    dragMode = DragMode.Both;

                    calcSlidingBound(x - lastXSlidingRect);
                    invalidate();
                    lastXSlidingRect = x;
                    return true;
                }


                showInfoBox = true;
                InfoBoxX = x;
                invalidate();

                getParent().requestDisallowInterceptTouchEvent(true);

                break;

            case MotionEvent.ACTION_UP:
                isDraggingLeftHandle = false;
                isDraggingRightHandle = false;
                isDraggingSlidingRect = false;
                lastXLeft = 0;
                lastXRight = 0;
                lastXSlidingRect = 0;
                showInfoBox = false;
                invalidate();
                break;
        }


        return true;
    }


    private void calcSlidingBound(float diffX) {
        switch (dragMode) {
            case LeftHandle:
                slidingRect.left += diffX;
                if (slidingRect.left < smallForegroundRect.left) {
                    slidingRect.left = smallForegroundRect.left;
                }
                if (diffX > 0 && slidingRect.width() <= slidingRectMinWith) {
                    slidingRect.left = slidingRect.right - slidingRectMinWith;
                }
                break;
            case RightHandle:
                slidingRect.right += diffX;
                if (slidingRect.right > smallForegroundRect.right) {
                    slidingRect.right = smallForegroundRect.right;
                    break;
                }

                if (diffX < 0 && slidingRect.width() < slidingRectMinWith) {
                    slidingRect.right = slidingRect.left + slidingRectMinWith;
                    break;
                }

                break;
            case Both:
                float w = slidingRect.width();
                slidingRect.left += diffX;
                if (slidingRect.left < smallForegroundRect.left) {
                    slidingRect.left = smallForegroundRect.left;
                    slidingRect.right = slidingRect.left + w;
                    break;
                }
                slidingRect.right += diffX;
                if (slidingRect.right > smallForegroundRect.right) {
                    slidingRect.right = smallForegroundRect.right;
                    slidingRect.left = slidingRect.right - w;
                    break;
                }
                break;
        }

        leftHandle.left = slidingRect.left;
        leftHandle.right = leftHandle.left + AndroidUtilities.dp(5);

        rightHandle.right = slidingRect.right;
        rightHandle.left = rightHandle.right - AndroidUtilities.dp(5);

    }


    private DragMode dragMode = DragMode.Both;

    public void setActiveChart(int index, boolean isChecked) {
        chartData.visibles[index] = isChecked;
        reNormalizeDataWithAnimation();
    }

    private void reNormalizeDataWithAnimation() {

        final float[] maxValue = {Integer.MIN_VALUE};
        for (int i = 0; i < chartData.yDataOriginal.size(); i++) {
            if (chartData.visibles[i]) {
                Integer[] ys = chartData.yDataOriginal.get(i);
                for (Integer y : ys) {
                    if (y > maxValue[0]) maxValue[0] = y;
                }
            }
        }

        if (maxValue[0] == Integer.MIN_VALUE) {
            maxValue[0] = 0;
        }
        if (lastMaxValue != maxValue[0]) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(lastMaxValue, maxValue[0]);
            lastMaxValue = maxValue[0];
            valueAnimator.setDuration(400);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    maxValue[0] = (float) animation.getAnimatedValue();

                    normalizeByMax(maxValue[0]);
                }
            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            valueAnimator.start();
        } else {
            // normalizeByMax(maxValue[0]);
            postInvalidate();
        }


    }

    private void normalizeByMax(float v) {
        chartData.yDataNormalized.clear();

        for (int i1 = 0; i1 < chartData.yDataOriginal.size(); i1++) {

            Integer[] ys = chartData.yDataOriginal.get(i1);
            Float[] yNormalized = new Float[ys.length];
            for (int i = 0; i < ys.length; i++) {

                float f = (float) (ys[i]) / v;
                yNormalized[i] = f;
            }
            chartData.yDataNormalized.add(yNormalized);
        }

        regenerate = true;
        invalidate();
    }

    enum DragMode {
        LeftHandle,
        RightHandle,
        Both
    }

    class Label {
        float x;
        float y;
        String text;//

        boolean visible;

        public Label(float x, float y, String text) {
            this.x = x;
            this.y = y;
            this.text = text;
        }

    }

    static class ChartData {
        String[] names;
        String title;
        List<Integer[]> yDataOriginal;
        List<Float[]> yDataNormalized;//y data are normalized (max of max y value mapped to 1 and other values mapped at same ratio)
        float xStep;//x direction step for path.LineTo
        long[] xDataOriginal;
        float[] xs;//x positions of data on screen
        boolean[] visibles;
        Label[] labels;

        void resetLabelsX() {
            for (Label label : labels) {
                label.x = Float.MAX_VALUE;
            }
        }
    }
}