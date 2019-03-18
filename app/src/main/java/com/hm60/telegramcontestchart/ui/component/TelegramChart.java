package com.hm60.telegramcontestchart.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hm60.telegramcontestchart.AndroidUtilities;

import java.util.ArrayList;
import java.util.List;

public class TelegramChart extends View {

    private float ratio = 11f;
    private float slidingRectRatio = 0.25f;
    private float slidingRectMinWith = AndroidUtilities.dp(20);

    private Paint[] paints;
    private Path[] paths;
    private Paint[] paintsSmall;
    private Path[] pathsSmall;


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

    private int minValue;
    private int maxValue;
    private int diff;

    private boolean regenerate = true;

    private RectF leftHandle = new RectF();
    private RectF rightHandle = new RectF();


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
        smallForegroundPaint.setColor(0x23D3CACA);

        handlesPaint.setStyle(Paint.Style.FILL);
        handlesPaint.setColor(0x5ED3CACA);
    }

    public void setData(List<Integer[]> yDataList, Long[] xData, String[] names, String[] colors, String[] types) {
        this.yDataListOriginal = yDataList;
        this.xData = xData;
        this.names = names;
        this.colors = colors;
        this.types = types;

        minValue = Integer.MAX_VALUE;
        maxValue = Integer.MIN_VALUE;

        for (Integer[] ys : yDataList) {
            for (Integer y : ys) {
                if (y > maxValue) maxValue = y;
                if (y < minValue) minValue = y;
            }
        }

        diff = maxValue - minValue;

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

        yDataListNormalized = new ArrayList<>(yDataList.size());
        for (Integer[] ys : yDataList) {
            Float[] yNormalized = new Float[ys.length];
            for (int i = 0; i < ys.length; i++) {
                float f = (float) (ys[i] - minValue) / diff;
                yNormalized[i] = f;
            }
            yDataListNormalized.add(yNormalized);
        }

        regenerate = true;

        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawSmallSection(canvas);

    }

    private void drawSmallSection(Canvas canvas) {

        float height = getHeight() / ratio;
        float top = getHeight() - 3 * height;
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
                Float[] yn = yDataListNormalized.get(i);
                Path path = pathsSmall[i];
                path.moveTo(left, top + (1 - yn[0]) * height);

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


        canvas.save();
        canvas.clipRect(slidingRect, Region.Op.DIFFERENCE);

        canvas.drawRect(smallForegroundRect, smallForegroundPaint);

        canvas.restore();

    }

    //    @SuppressLint("ClickableViewAccessibility")
    //    override fun onTouchEvent(event: MotionEvent): Boolean {
    //        if (!isEnabled) {
    //            return false
    //        }
    //
    //        val action = event.action and MotionEvent.ACTION_MASK
    //
    //        when (action) {
    //
    //            MotionEvent.ACTION_DOWN -> {
    //                val x = event.x
    //                val y = event.y
    //                val threshold = toPxF(10f)
    //
    //                if (RectF(x - threshold, dragHandLeft.top, x + threshold, dragHandLeft.bottom).contains(dragHandLeft)) {
    //                    mIsDragging1 = true
    //                    mIsDragging2 = false
    //                    mIsDraggingTransparentSection = false
    //                    mTouchDownX1 = x
    //                    hotSpotPoint.x = x
    //                    hotSpotPoint.y = y
    //                    animateHotSpot()
    //
    //                    return true
    //                }
    //
    //                if (RectF(
    //                        x - threshold,
    //                        dragHandRight.top,
    //                        x + threshold,
    //                        dragHandRight.bottom
    //                    ).contains(dragHandRight)
    //                ) {
    //                    mIsDragging1 = false
    //                    mIsDragging2 = true
    //                    mIsDraggingTransparentSection = false
    //                    mTouchDownX2 = x
    //                    hotSpotPoint.x = x
    //                    hotSpotPoint.y = y
    //                    animateHotSpot()
    //
    //                    return true
    //                }
    //
    //                if (x > dragHandLeft.right && x < dragHandRight.left &&
    //                    y > dragHorBorderTop.bottom && y < dragHorBorderBottom.top
    //                ) {
    //                    mIsDragging1 = false
    //                    mIsDragging2 = false
    //                    mIsDraggingTransparentSection = true
    //                    mTouchDownTransparentSection = x
    //                    hotSpotPoint.x = x
    //                    hotSpotPoint.y = y
    //                    animateHotSpot()
    //
    //                    return true
    //                }
    //
    //                return false
    //
    //            }
    //
    //            MotionEvent.ACTION_MOVE -> {
    //                when {
    //                    mIsDragging1 -> {
    //                        val x = event.x
    //                        calcHandleLeft(x - mTouchDownX1)
    //                        invalidate()
    //                        mTouchDownX1 = x
    //                    }
    //                    mIsDragging2 -> {
    //                        val x = event.x
    //                        calcHandleRight(x - mTouchDownX2)
    //                        invalidate()
    //                        mTouchDownX2 = x
    //                    }
    //                    mIsDraggingTransparentSection -> {
    //                        val x = event.x
    //
    //                        val xDiff = x - mTouchDownTransparentSection
    //                        if (calcHandleLeft(xDiff)) {
    //                            calcHandleRight(xDiff, false)
    //                            invalidate()
    //                            mTouchDownTransparentSection = x
    //                        }
    //
    //                    }
    //                }
    //            }
    //
    //            MotionEvent.ACTION_UP -> {
    //                mIsDragging1 = false
    //                mIsDragging2 = false
    //                mIsDraggingTransparentSection = false
    //                animateHotSpot(true)
    //                invalidate()
    //            }
    //        }
    //
    //
    //        return true
    //    }


    boolean isDraggingLeftHandle;
    boolean isDraggingRightHandle;
    boolean isDraggingSlidingRect;
    float lastXLeft;
    float lastXRight;
    float lastXSlidingRect;


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

                    return true;
                }

                if (y >= rightHandle.top && y <= rightHandle.bottom &&
                        Math.abs(x - rightHandle.centerX()) <= threshold) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = true;
                    isDraggingSlidingRect = false;
                    lastXRight = x;

                    return true;
                }

                if (slidingRect.contains(x,y)) {
                    isDraggingLeftHandle = false;
                    isDraggingRightHandle = false;
                    isDraggingSlidingRect = true;
                    lastXSlidingRect = x;

                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (isDraggingLeftHandle){
                    calcSlidingBound(x - lastXLeft, CalcMode.LeftHandle);
                    invalidate();
                    lastXLeft = x;
                    return true;
                }

                if (isDraggingRightHandle){
                    calcSlidingBound(x - lastXRight, CalcMode.RightHandle);
                    invalidate();
                    lastXRight = x;
                    return true;
                }

                if (isDraggingSlidingRect){
                    calcSlidingBound(x - lastXSlidingRect, CalcMode.Both);
                    invalidate();
                    lastXSlidingRect = x;
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP | MotionEvent.ACTION_CANCEL:
                isDraggingLeftHandle = false;
                isDraggingRightHandle = false;
                isDraggingSlidingRect = false;
                lastXLeft = 0;
                lastXRight = 0;
                lastXSlidingRect = 0;
                invalidate();
                break;
        }


        return true;
    }

    private void calcSlidingBound(float diffX, CalcMode mode) {
        switch (mode){
            case LeftHandle:
                slidingRect.left += diffX;
                if (slidingRect.left < smallForegroundRect.left){
                    slidingRect.left = smallForegroundRect.left;
                }
                if (diffX > 0 && slidingRect.width() <= slidingRectMinWith){
                    slidingRect.left = slidingRect.right - slidingRectMinWith;
                }
                break;
            case RightHandle:
                slidingRect.right += diffX;
                if (slidingRect.right > smallForegroundRect.right){
                    slidingRect.right = smallForegroundRect.right;
                }

                if (diffX < 0 && slidingRect.width() < slidingRectMinWith){
                    slidingRect.right = slidingRect.left + slidingRectMinWith;
                }

                break;
            case Both:
                float w = slidingRect.width();
                slidingRect.left += diffX;
                if (slidingRect.left < smallForegroundRect.left){
                    slidingRect.left = smallForegroundRect.left;
                    slidingRect.right = slidingRect.left + w;
                    break;
                }
                slidingRect.right += diffX;
                if (slidingRect.right > smallForegroundRect.right){
                    slidingRect.right = smallForegroundRect.right;
                    slidingRect.left = slidingRect.right - w;
                }
                break;
        }

        leftHandle.left = slidingRect.left;
        leftHandle.right = leftHandle.left + AndroidUtilities.dp(5);

        rightHandle.right = slidingRect.right;
        rightHandle.left = rightHandle.right - AndroidUtilities.dp(5);

    }

    enum CalcMode{
        LeftHandle,
        RightHandle,
        Both
    }
}