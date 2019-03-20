package com.hm60.telegramcontestchart.ui.component;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hm60.telegramcontestchart.AndroidUtilities;
import com.hm60.telegramcontestchart.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TelegramChart extends View {

    private float ratio = 11f;
    private float slidingRectRatio = 0.3f;// 0.3 of progress section width
    private float slidingRectMinWith = AndroidUtilities.dp(20);

    private Paint backLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    boolean showTooltip;
    float tooltipX;
    private Path[] tooltipPaths;
    private Paint indicatorCirclePaint = new Paint();
    private Path indicatorLinePath = new Path();

    private Paint[] paints;
    private Path[] paths;
    private Paint[] paintsSmall;
    private Path[] pathsSmall;
    private TextPaint textPaint = new TextPaint();


    private List<Integer[]> yDataListOriginal;
    private Long[] xData;
    private String[] names;
    private String[] colors;
    private String[] types;
    private List<Float[]> yDataListNormalized;

    private RectF slidingRect = new RectF();
    private RectF smallForegroundRect = new RectF();

    private boolean isInit;

    private Paint smallForegroundPaint = new Paint();
    private Paint handlesPaint = new Paint();

    private int maxValue;
    private int diff;

    private boolean regenerate = true;

    private RectF leftHandle = new RectF();
    private RectF rightHandle = new RectF();

    private ObjectAnimator circleRadiusAnimator;

    private float circleRadius;
    private PointF circlePoint = new PointF();
    private int xAxisColor = 0xFFE1E2E4;
    private int backgroundLinesColor = 0xFFEDEDEF;

    @Keep
    public void setCircleRadius(float circleRadius) {
        if (this.circleRadius == circleRadius) {
            return;
        }

        this.circleRadius = circleRadius;
        invalidate();
    }

    boolean isDraggingLeftHandle;
    boolean isDraggingRightHandle;
    boolean isDraggingSlidingRect;
    float lastXLeft;
    float lastXRight;
    float lastXSlidingRect;

    private boolean[] activeCharts;

    private Label[] xLabels;


    public TelegramChart(Context context) {
        super(context);

        init();
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        smallForegroundPaint.setStyle(Paint.Style.FILL);
        smallForegroundPaint.setColor(0xA1F6F8FE);

        handlesPaint.setStyle(Paint.Style.FILL);
        handlesPaint.setColor(0x5ED3CACA);

        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(0x88ECECF5);

        backLinesPaint.setColor(xAxisColor);
        backLinesPaint.setStyle(Paint.Style.STROKE);

        textPaint.setColor(0xFF84919A);
        textPaint.setTextSize(AndroidUtilities.dp(10));

        indicatorCirclePaint.setColor(0xFFFFFFFF);
        indicatorCirclePaint.setStyle(Paint.Style.FILL);

    }

    public void setData(List<Integer[]> yDataList, Long[] xData, String[] names, String[] colors, String[] types) {
        this.yDataListOriginal = yDataList;
        this.xData = xData;
        this.names = names;
        this.colors = colors;
        this.types = types;

        maxValue = Integer.MIN_VALUE;

        for (Integer[] ys : yDataList) {
            for (Integer y : ys) {
                if (y > maxValue) maxValue = y;
            }
        }

        diff = maxValue;

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

        tooltipPaths = new Path[yDataList.size()];
        for (int i = 0; i < tooltipPaths.length; i++) {
            tooltipPaths[i] = new Path();
        }

        yDataListNormalized = new ArrayList<>(yDataList.size());
        for (Integer[] ys : yDataList) {
            Float[] yNormalized = new Float[ys.length];
            for (int i = 0; i < ys.length; i++) {
                float f = (float) (ys[i]) / diff;
                yNormalized[i] = f;
            }
            yDataListNormalized.add(yNormalized);
        }

        activeCharts = new boolean[yDataList.size()];
        for (int i = 0; i < activeCharts.length; i++) {
            activeCharts[i] = true;
        }

        //activeCharts[0] = false;

        xLabels = new Label[xData.length];
        for (int i = 0; i < xData.length; i++) {
            Label label = new Label(0, 0, Utils.toShortDateString(xData[i]));
            xLabels[i] = label;
        }

        regenerate = true;

        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 255, 255, 255);

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

            for (int i = 0; i < yDataListNormalized.size(); i++) {
                boolean activeChart = activeCharts[i];
                if (!activeChart) {
                    continue;
                }
                Float[] yn = yDataListNormalized.get(i);
                Path path = pathsSmall[i];
                path.moveTo(left, top + (1 - yn[0]) * height);

                //path.offset();

                for (int i1 = 1; i1 < yn.length; i1++) {

                    float y = top + (1 - yn[i1]) * height;
                    float x = left + (float) i1 * width / (yn.length - 1);

                    path.lineTo(x, y);
                }

            }
        }


        for (int i = 0; i < pathsSmall.length; i++) {
            canvas.drawPath(pathsSmall[i], paintsSmall[i]);
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

        canvas.drawCircle(circlePoint.x, circlePoint.y, circleRadius, circlePaint);

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

        for (Label xLabel : xLabels) {
            xLabel.y = bottom - AndroidUtilities.dp(25);
        }

        backLinesPaint.setStrokeWidth(0);

        //xAxis
        backLinesPaint.setColor(xAxisColor);
        canvas.drawLine(left,
                bottom - AndroidUtilities.dp(40),
                right,
                bottom - AndroidUtilities.dp(40),
                backLinesPaint);

        //background lines
        backLinesPaint.setColor(backgroundLinesColor);

        for (int i = 1; i <= 5; i++) {
            int y = (int) (bottom - smallSectionHeight - height / 6 * i);

            canvas.drawLine(left,
                    y,
                    right,
                    y,
                    backLinesPaint);
        }

        indicatorLinePath.reset();

        for (Path tooltipPath : tooltipPaths) {
            tooltipPath.reset();
        }

        for (Path path : paths) {
            path.reset();
        }

        int indexFrom = 0;
        int indexTo = 0;

        switch (dragMode) {

            case LeftHandle:
            case RightHandle:
                indexFrom = (int) ((leftHandle.left - AndroidUtilities.dp(16)) / smallForegroundRect.width() * (xData.length - 1));
                indexTo = (int) ((rightHandle.right - AndroidUtilities.dp(16)) / smallForegroundRect.width() * (xData.length - 1));
                break;
            case Both:
                //(indexTo - indexFrom) should be constant at this point
                indexFrom = (int) ((leftHandle.left - AndroidUtilities.dp(16)) / smallForegroundRect.width() * (xData.length - 1));
                indexTo = (int) (indexFrom + 1 + slidingRect.width() / smallForegroundRect.width() * (xData.length - 1));
                break;
        }

        //draw xAxis labels
/*        int count = indexTo - indexFrom;
        int step = count / 6;
        int Y = (int) (bottom - AndroidUtilities.dp(25));
        for (int i = 0; i < 6; i++) {
            String dateString = Utils.toShortDateString(xData[indexTo - i * step]);

            int k = indexTo - i * step - xData.length + 1;

            int x = (int) (right + (k - 1) * AndroidUtilities.dp(30));

            canvas.drawText(dateString, x, Y, textPaint);

        }*/

/*        for (Label xLabel : xLabels) {
            xLabel.visible = false;
        }



        for (int i = 0; i < yDataListNormalized.size(); i++) {
            Float[] yn = yDataListNormalized.get(i);
            Path path = paths[i];

            path.moveTo(left, top + (1 - yn[indexFrom]) * (height - AndroidUtilities.dp(40)));
            xLabels[indexFrom].x = left;
            xLabels[indexFrom].visible = true;

            for (int i1 = indexFrom + 1; i1 <= indexTo; i1++) {

                float y = top + (1 - yn[i1]) * (height - AndroidUtilities.dp(40));
                float x = left + (float) (i1 - indexFrom) * width / (indexTo - indexFrom);

                xLabels[i1].x = x;
                xLabels[i1].visible = true;

                path.lineTo(x, y);

                if (showTooltip) {

                    int index = indexFrom + Math.round((tooltipX - left) / width * (indexTo - indexFrom));
                    if (index == i1) {

                        if (i == 0) {// before first circle added, so move to starting point
                            indicatorLinePath.moveTo(x, bottom - AndroidUtilities.dp(40));
                        }
                        indicatorLinePath.lineTo(x, y);
                        Path tooltipPath = tooltipPaths[i];
                        tooltipPath.addCircle(x, y, AndroidUtilities.dp(4), Path.Direction.CW);

                        if ((i == yDataListNormalized.size() - 1)) {//last circle added, so line to ending point
                            indicatorLinePath.lineTo(x, paddingTop);
                        }
                    }

                }

            }

        }

        for (Label xLable : xLabels) {
            if (xLable.visible) {
                canvas.drawText(xLable.text, xLable.x, xLable.y, textPaint);
            }
        }*/


        float ratio = smallForegroundRect.width() / slidingRect.width();

        for (int i = 0; i < yDataListNormalized.size(); i++) {
            Float[] yn = yDataListNormalized.get(i);
            Path path = paths[i];

            for (int i1 = 0; i1 < yn.length; i1++) {

                float x = 0;
                float y = top + (1 - yn[i1]) * (height - AndroidUtilities.dp(40));

                switch (dragMode) {
                    case LeftHandle:
                        int p = (int) ((rightHandle.right - AndroidUtilities.dp(16)) / smallForegroundRect.width() * (yn.length - 1));
                        int q = (int) ((leftHandle.left - AndroidUtilities.dp(16)) / smallForegroundRect.width() * (yn.length - 1));

                        x = (right - (float) (i1 - yn.length + 1) / (p - yn.length + 1) * (right) - (xDiff * ratio));
                        x = width - (float) (i1 - p) / (q - p) * width;

                        break;
                    case RightHandle:

                        break;
                    case Both:
                        int tt = (int) ((yn.length - 1) * (1 - 1 / ratio));

                        x = (right - (float) (i1 - yn.length + 1) / (tt - yn.length + 1) * (right) - (xDiff * ratio));

                        break;
                }


                if (i1 == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }

        }


        for (int i = 0; i < paths.length; i++) {
            canvas.drawPath(paths[i], paints[i]);
        }

        backLinesPaint.setColor(0xFFDFE6EB);
        backLinesPaint.setStrokeWidth(AndroidUtilities.dp(1));
        canvas.drawPath(indicatorLinePath, backLinesPaint);

        for (int i = 0; i < tooltipPaths.length; i++) {
            canvas.drawPath(tooltipPaths[i], indicatorCirclePaint);
            canvas.drawPath(tooltipPaths[i], paints[i]);
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
                    circlePoint.x = x;
                    circlePoint.y = y;
                    startCircleAnimation(false);
                    return true;
                }

                if (y >= rightHandle.top && y <= rightHandle.bottom &&
                        Math.abs(x - rightHandle.centerX()) <= threshold) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = true;
                    isDraggingSlidingRect = false;
                    lastXRight = x;
                    circlePoint.x = x;
                    circlePoint.y = y;
                    startCircleAnimation(false);
                    return true;
                }

                if (slidingRect.contains(x, y)) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = false;
                    isDraggingSlidingRect = true;
                    lastXSlidingRect = x;
                    circlePoint.x = x;
                    circlePoint.y = y;
                    startCircleAnimation(false);
                    return true;
                }

                showTooltip = true;
                tooltipX = x;
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

                showTooltip = true;
                tooltipX = x;
                invalidate();

                break;

            case MotionEvent.ACTION_UP:
                if (isDraggingSlidingRect || isDraggingRightHandle || isDraggingLeftHandle) {
                    startCircleAnimation(true);
                }
                isDraggingLeftHandle = false;
                isDraggingRightHandle = false;
                isDraggingSlidingRect = false;
                lastXLeft = 0;
                lastXRight = 0;
                lastXSlidingRect = 0;
                showTooltip = false;
                invalidate();
                break;
        }


        return true;
    }

    float xDiff;

    private void calcSlidingBound(float diffX) {
        switch (dragMode) {
            case LeftHandle:
                slidingRect.left += diffX;
                circlePoint.x += diffX;
                if (slidingRect.left < smallForegroundRect.left) {
                    slidingRect.left = smallForegroundRect.left;
                    circlePoint.x = leftHandle.centerX();
                }
                if (diffX > 0 && slidingRect.width() <= slidingRectMinWith) {
                    slidingRect.left = slidingRect.right - slidingRectMinWith;
                    circlePoint.x = leftHandle.centerX();
                }
                break;
            case RightHandle:
                slidingRect.right += diffX;
                circlePoint.x += diffX;
                if (slidingRect.right > smallForegroundRect.right) {
                    slidingRect.right = smallForegroundRect.right;
                    circlePoint.x = rightHandle.centerX();
                }

                if (diffX < 0 && slidingRect.width() < slidingRectMinWith) {
                    slidingRect.right = slidingRect.left + slidingRectMinWith;
                    circlePoint.x = rightHandle.centerX();
                }

                break;
            case Both:
                float w = slidingRect.width();
                slidingRect.left += diffX;
                if (slidingRect.left < smallForegroundRect.left) {
                    slidingRect.left = smallForegroundRect.left;
                    slidingRect.right = slidingRect.left + w;
                    this.xDiff = -(smallForegroundRect.width() - w);
                    break;
                }
                slidingRect.right += diffX;
                if (slidingRect.right > smallForegroundRect.right) {
                    slidingRect.right = smallForegroundRect.right;
                    slidingRect.left = slidingRect.right - w;
                    this.xDiff = 0;
                    break;
                }
                circlePoint.x += diffX;
                this.xDiff += diffX;
                break;
        }

        leftHandle.left = slidingRect.left;
        leftHandle.right = leftHandle.left + AndroidUtilities.dp(5);

        rightHandle.right = slidingRect.right;
        rightHandle.left = rightHandle.right - AndroidUtilities.dp(5);


    }


    private void startCircleAnimation(boolean revers) {
        if (circleRadiusAnimator == null) {
            circleRadiusAnimator = ObjectAnimator.ofFloat(this, "circleRadius", 0, smallForegroundRect.height() / 2 * 1.8f);
            circleRadiusAnimator.setDuration(200);
        }

        if (revers) {
            circleRadiusAnimator.reverse();
        } else {
            circleRadiusAnimator.start();
        }
    }

    private DragMode dragMode = DragMode.Both;

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
}