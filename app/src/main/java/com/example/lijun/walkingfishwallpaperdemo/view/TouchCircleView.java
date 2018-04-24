package com.example.lijun.walkingfishwallpaperdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.lijun.walkingfishwallpaperdemo.BuildConfig;
import com.example.lijun.walkingfishwallpaperdemo.listener.ICircleDisappearListener;
import com.example.lijun.walkingfishwallpaperdemo.util.ScreenUtil;

public class TouchCircleView {
    private final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "TouchCircleView";
    private float mTouchPositionX;
    private float mTouchPositionY;
    private float mCircleRadius = 20;
    private Paint mPaint;
    private final int mCircleRadiateRate;
    private ICircleDisappearListener mDisappearListener;

    public TouchCircleView(Context context, ICircleDisappearListener disappearListener) {
        this.mDisappearListener = disappearListener;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setStrokeWidth(2f);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStyle(Paint.Style.STROKE);
        mCircleRadiateRate = ScreenUtil.dip2px(context, 4);
    }

    public void setTouchPosition(float touchPositionX, float touchPositionY) {
        this.mTouchPositionX = touchPositionX;
        this.mTouchPositionY = touchPositionY;
    }

    public void drawCircle(Canvas canvas) {
        if (DEBUG) {
            Log.d(TAG, " drawCircle() ");
        }
        canvas.drawCircle(mTouchPositionX, mTouchPositionY, mCircleRadius, mPaint);
        mCircleRadius += mCircleRadiateRate;
        int alpha = (int) (mPaint.getAlpha() * 0.95f);
        mPaint.setAlpha(alpha);
        if (alpha == 0) {
            mDisappearListener.circleDisappear();
        }
    }
}
