package com.xyp.tiange.windowloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * User: xyp
 * Date: 2017/7/17
 * Time: 15:33
 */

public class LoadingView extends View {
    private Paint mPaint;
    private Path mPath, dst;//dst用来存放截取的path某一段的坐标，每次调用getSegment如果不reset的话dst会保存多段的路径坐标
    private PathMeasure mPathMeasure;
    private int measureWidth, measureHeight;
    private float time;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAni();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(16);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        //圆笔
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath = new Path();
        RectF rect = new RectF(-150, -150, 150, 150);
        mPath.addArc(rect, -90, 359.9f);

        mPathMeasure = new PathMeasure(mPath, false);
        dst = new Path();
    }

    private void initAni() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                time = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureWidth = w;
        measureHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(measureWidth / 2, measureHeight / 2);
        if (time >= 0.95) {
            canvas.drawPoint(0, -150, mPaint);//解决最后闪一下的问题
        }
        setDst();
        canvas.drawPath(dst, mPaint);
    }

    private void setDst() {
        dst.reset();
        Log.d("TAG", "time:" + time);
        //每隔0.05画一个点,画4个。
        int num = (int) (time / 0.05);//num是第几个点
        float s, y, x;
        //没有break，目的是执行多次getSegment，每次都有4个点的坐标存放在dst中
        switch (num) {
            default:
            case 3:
                x = time - 0.15f * (1 - time);
                s = mPathMeasure.getLength();
                y = -s * x * x + 2 * s * x;
                mPathMeasure.getSegment(y, y + 1, dst, true);
            case 2:
                x = time - 0.10f * (1 - time);
                s = mPathMeasure.getLength();
                y = -s * x * x + 2 * s * x;
                mPathMeasure.getSegment(y, y + 1, dst, true);
            case 1:
                x = time - 0.05f * (1 - time);
                s = mPathMeasure.getLength();
                y = -s * x * x + 2 * s * x;
                mPathMeasure.getSegment(y, y + 1, dst, true);
            case 0:
                x = time;
                s = mPathMeasure.getLength();
                y = -s * x * x + 2 * s * x;
                mPathMeasure.getSegment(y, y + 1, dst, true);
                break;
        }
    }
}
