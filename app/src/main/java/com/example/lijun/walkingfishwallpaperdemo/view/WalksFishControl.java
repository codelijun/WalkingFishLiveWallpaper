package com.example.lijun.walkingfishwallpaperdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.example.lijun.walkingfishwallpaperdemo.BuildConfig;
import com.example.lijun.walkingfishwallpaperdemo.util.ScreenUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class WalksFishControl {
    private final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "WalksFishControl";

    private static final int FISH_SELF_SWING_DURATION = 1200;   //鱼自身摆动一个周期耗费的时间
    private static final int FISH_WALKS_DISTANCE = 1000;         //鱼游动的目标距离,不要更改
    private static final int FISH_ANGLE_CHANGE_DURATION = 500;  //鱼转向耗费的时间
    private static final int FISH_ADVANCE_DURATION = 1000;      //鱼前进一段距离耗费的时间,和FISH_WALKS_DISTANCE字段共同决定鱼的速度

    private WalksFishView mWalksFishView;
    private ValueAnimator mFishWalksAnimator;   //鱼身体游动的动画
    private ValueAnimator mAngleAnimator;       //鱼角度转变的动画
    private ValueAnimator mFishAccelerateAnimator; //鱼被点击,突然加速再减速的动画
    private float mCurrentAngle;
    private float mTargetAngle;
    private int mWindowWidth;
    private int mWindowHeight;
    private int mFishWalksDistance;
    private long mFishWalksDuration;
    private PointF mCurrentPosition;
    private PointF mPreviousPosition;
    private PointF mTargetPosition = new PointF();

    @IntDef({NORMAL_SPEED, FAST_SPEED, SLOW_SPEED, SLIGHTLY_FAST_SPEED, SLIGHTLY_SLOW_SPEED})
    @Retention(RetentionPolicy.SOURCE)
    private @interface FishWalksSpeed {
    }

    public static final int NORMAL_SPEED = 0; //默认
    public static final int FAST_SPEED = 1;
    public static final int SLOW_SPEED = 2;
    public static final int SLIGHTLY_FAST_SPEED = 4;
    public static final int SLIGHTLY_SLOW_SPEED = 5;

    public WalksFishControl(Context context) {
        mWindowWidth = ScreenUtil.getScreenWidth(context);
        mWindowHeight = ScreenUtil.getScreenHeight(context);
        mFishWalksDistance = ScreenUtil.dip2px(context, FISH_WALKS_DISTANCE);
        mFishWalksDuration = FISH_ADVANCE_DURATION;

        mWalksFishView = new WalksFishView(context);
        mWalksFishView.setMainAngle(120);
        mWalksFishView.setCurrentValue(90);
        initAnimator();
    }

    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            setRandomPosition();
            mFishWalksAnimator.start();
        } else {
            mFishWalksAnimator.cancel();
        }
    }

    public void setFishWalksSpeed(@FishWalksSpeed int fishWalksSpeed) {
        switch (fishWalksSpeed) {
            case FAST_SPEED:
                mFishWalksDuration = (long) (FISH_WALKS_DISTANCE * 0.4f);
                break;
            case SLIGHTLY_FAST_SPEED:
                mFishWalksDuration = (long) (FISH_WALKS_DISTANCE * 0.6f);
                break;
            case SLOW_SPEED:
                mFishWalksDuration = (long) (FISH_WALKS_DISTANCE * 2f);
                break;
            case SLIGHTLY_SLOW_SPEED:
                mFishWalksDuration = (long) (FISH_WALKS_DISTANCE * 1.5f);
                break;
            default:
                break;
        }
    }

    public void setFishColor(int color) {
        mWalksFishView.setFishColor(color);
    }

    public void setFishAge(int age) {
        mWalksFishView.setFishAge(age);
    }

    /**
     * 初始化,产生鱼的一个随机位置
     */
    public void setRandomPosition() {
        float positionX = (int) (Math.random() * mWindowWidth);
        float positionY = (int) (Math.random() * mWindowHeight);
        mCurrentPosition = new PointF(positionX, positionY);
        mPreviousPosition = new PointF(positionX, positionY);
        mWalksFishView.setFishCurrentPosition(positionX, positionY);
    }

    public PointF getCurrentPosition() {
        return mCurrentPosition;
    }

    public int getFishHeadRadius() {
        return mWalksFishView.getFishHeadRadius();
    }

    /**
     * 设置鱼前进的目标位置
     *
     * @param positionX
     * @param positionY
     */
    private void setTargetPosition(float positionX, float positionY) {
        mTargetPosition.x = positionX;
        mTargetPosition.y = positionY;
    }

    private void initAnimator() {
        //转向目标角度的动画
        mAngleAnimator = ValueAnimator.ofFloat(0, 1);
        mAngleAnimator.setDuration(FISH_ANGLE_CHANGE_DURATION);
        mAngleAnimator.setInterpolator(new LinearInterpolator());
        mAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float temp = (float) animation.getAnimatedValue();
                float currentAngle = temp * mTargetAngle + mCurrentAngle;
                mWalksFishView.setMainAngle(currentAngle);
                PointF targetPosition = ScreenUtil.calculatPoint(mCurrentPosition, mFishWalksDistance, currentAngle);
                setTargetPosition(targetPosition.x, targetPosition.y);
            }
        });
        mAngleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mPreviousPosition.x = mCurrentPosition.x;
                mPreviousPosition.y = mCurrentPosition.y;
            }
        });

        //鱼自身摆动的动画
        mFishWalksAnimator = ValueAnimator.ofInt(0, 360);
        mFishWalksAnimator.setDuration(FISH_SELF_SWING_DURATION);
        mFishWalksAnimator.setInterpolator(new LinearInterpolator());
        mFishWalksAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mFishWalksAnimator.setRepeatMode(ValueAnimator.RESTART);
        mFishWalksAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mWalksFishView != null) {
                    mWalksFishView.setCurrentValue((Integer) animation.getAnimatedValue());
                }
            }
        });

        mFishAccelerateAnimator = ValueAnimator.ofFloat(0, 1);
        mFishAccelerateAnimator.setDuration(2000);
        mFishAccelerateAnimator.setInterpolator(new LinearInterpolator());
        mFishAccelerateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float temp = (float) animation.getAnimatedValue();
                float speedChangeOffset = temp * 0.8f + 0.2f;  //[0.2,1]
                mFishWalksDuration = (long) (FISH_WALKS_DISTANCE * speedChangeOffset);
            }
        });
    }

    public void startFishShyAnimator() {
        mFishAccelerateAnimator.cancel();
        mFishAccelerateAnimator.start();
    }

    private void turnToAngle(float targetAngle) {
        mTargetAngle = targetAngle;
        mAngleAnimator.cancel();
        mAngleAnimator.start();
    }

    public void updateFishPosition() {
        float speedX = (mTargetPosition.x - mPreviousPosition.x) / mFishWalksDuration;
        float speedY = (mTargetPosition.y - mPreviousPosition.y) / mFishWalksDuration;
        mCurrentPosition.x += speedX;
        mCurrentPosition.y += speedY;
        mWalksFishView.setFishCurrentPosition(mCurrentPosition.x, mCurrentPosition.y);
    }

    /**
     * 更新鱼前进的方向
     */
    public void updateFishAngle() {
        mCurrentAngle = mWalksFishView.getMainAngle();
        float angle = obtainSuitableAngle();
        turnToAngle(angle);
    }

    /**
     * 根据鱼当前的位置获取一个合适的角度
     *
     * @return
     */
    private float obtainSuitableAngle() {
        float angle;
        double angleCosine = Math.cos(Math.toRadians(mCurrentAngle));
        double angleSine = Math.sin(Math.toRadians(mCurrentAngle));
        if (DEBUG) {
            Log.d(TAG, " obtainSuitableAngle() mCurrentAngle== " + mCurrentAngle
                    + " angleCosine== " + angleCosine + " angleSine== " + angleSine);
        }
        //如果用户点击,鱼头就指向点击的位置
//        if (touchPositionX > 0 && touchPositionY > 0) {
//            float slope = (float) Math.atan2(touchPositionX - mCurrentPosition.x, touchPositionY - mCurrentPosition.y);
//            angle = (float) (Math.toDegrees(slope) - 90 - mCurrentAngle);
//            return angle;
//        }
        if (mCurrentPosition.x < 0 && angleCosine < 0 && angleCosine > -1) { //鱼当前的位置在屏幕的左边,并且鱼头指向左边
            if (DEBUG) {
                Log.d(TAG, " obtainSuitableAngle()在屏幕的左边 角度偏上: " + (angleSine > 0));
            }
            angle = (angleSine > 0 ? -90 : 90);
        } else if (mCurrentPosition.x > mWindowWidth && angleCosine > 0 && angleCosine < 1) { //鱼当前位置在屏幕的右边,并且鱼头指向右边
            if (DEBUG) {
                Log.d(TAG, " obtainSuitableAngle()在屏幕的右边 角度偏上: " + (angleSine > 0));
            }
            angle = (angleSine > 0 ? 90 : -90);
        } else if (mCurrentPosition.y < 0 && angleSine > 0 && angleSine < 1) { //鱼当前位置在屏幕的上方,并且鱼头指向上方
            if (DEBUG) {
                Log.d(TAG, " obtainSuitableAngle()在屏幕的上边 角度偏右: " + (angleCosine > 0));
            }
            angle = (angleCosine > 0 ? -90 : 90);
        } else if (mCurrentPosition.y > mWindowHeight && angleSine < 0 && angleSine > -1) {//鱼当前位置在屏幕的下方,并且鱼头指向下方
            if (DEBUG) {
                Log.d(TAG, " obtainSuitableAngle()在屏幕的下边 角度偏右: " + (angleCosine > 0));
            }
            angle = (angleCosine > 0 ? 90 : -90);
        } else {
            if (DEBUG) {
                Log.d(TAG, " obtainSuitableAngle() 在屏幕范围内");
            }
            angle = (float) (Math.random() * 90 - 45); //单位:角度,范围:[-45,45]
        }
        if (DEBUG) {
            Log.d(TAG, " obtainSuitableAngle() angle== " + angle);
        }
        return angle;
    }

    public void drawFish(Canvas canvas) {
        mWalksFishView.drawFish(canvas);
    }
}
