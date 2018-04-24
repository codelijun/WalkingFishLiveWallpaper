package com.example.lijun.walkingfishwallpaperdemo.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.lijun.walkingfishwallpaperdemo.BuildConfig;
import com.example.lijun.walkingfishwallpaperdemo.FishSettingActivity;
import com.example.lijun.walkingfishwallpaperdemo.listener.ICircleDisappearListener;

import java.util.ArrayList;
import java.util.List;

public class WalksFishManager implements ICircleDisappearListener {
    private final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "WalksFishManager";
    private static final int FISH_OBTAIN_ANGLE_DURATION = 3000; //鱼产生随机角度的间隔时间,每5秒产生一个随机角度
    private static final String DEFAULT_FISH_NUM = "001";
    private static final int RED_FISH_COLOR = 0xFFFF4500;
    private static final int YELLOW_FISH_COLOR = 0xFFFFD700;
    private static final int BLANK_FISH_COLOR = 0xFF000000;
    private boolean mVisible;
    private boolean mEnableTouch;
    private int mRedFishPreviousNum;
    private int mYellowFishPreviousNum;
    private int mBlankFishPreviousNum;
    private float mTouchPositionX = -1;
    private float mTouchPositionY = -1;
    List<WalksFishControl> mRedWalksFishList = new ArrayList<>();
    List<WalksFishControl> mYellowWalksFishList = new ArrayList<>();
    List<WalksFishControl> mBlankWalksFishList = new ArrayList<>();

    private Handler mHandler = new Handler();
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private TouchCircleView mTouchCircleView;

    public WalksFishManager(Context context, SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
        this.mContext = context;
    }

    public void onVisibilityChanged(boolean visible) {
        mVisible = visible;
        updateFishNum();

        for (WalksFishControl firstWalksFish : mRedWalksFishList) {
            firstWalksFish.onVisibilityChanged(visible);
        }

        for (WalksFishControl firstWalksFish : mYellowWalksFishList) {
            firstWalksFish.onVisibilityChanged(visible);
        }

        for (WalksFishControl firstWalksFish : mBlankWalksFishList) {
            firstWalksFish.onVisibilityChanged(visible);
        }

        if (visible) {
            mHandler.post(mDrawTargetRun);
            mHandler.post(mObtainAngle);
            mHandler.post(mAdvanceRun);
        } else {
            mHandler.removeCallbacks(mDrawTargetRun);
            mHandler.removeCallbacks(mObtainAngle);
            mHandler.removeCallbacks(mAdvanceRun);
        }
    }

    private void updateFishNum() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEnableTouch = sharedPreferences.getBoolean(FishSettingActivity.TOUCH_ENABLE_KEY, true);

        String redFishNum = sharedPreferences.getString(FishSettingActivity.RED_FISH_KEY, DEFAULT_FISH_NUM);
        int currentRedFishNum = Integer.valueOf(redFishNum);
        if (mRedFishPreviousNum != currentRedFishNum) {
            mRedWalksFishList.clear();
            for (int i = 0; i < currentRedFishNum; i++) {
                WalksFishControl redWalksFish = new WalksFishControl(mContext);
                redWalksFish.setFishColor(RED_FISH_COLOR);
                mRedWalksFishList.add(redWalksFish);
            }
            mRedFishPreviousNum = currentRedFishNum;
        }

        String yellowFishNum = sharedPreferences.getString(FishSettingActivity.YELLOW_FISH_KEY, DEFAULT_FISH_NUM);
        int currentYellowFishNum = Integer.valueOf(yellowFishNum);
        if (mYellowFishPreviousNum != currentYellowFishNum) {
            mYellowWalksFishList.clear();
            for (int i = 0; i < currentYellowFishNum; i++) {
                WalksFishControl yellowWalksFish = new WalksFishControl(mContext);
                yellowWalksFish.setFishColor(YELLOW_FISH_COLOR);
                mYellowWalksFishList.add(yellowWalksFish);
            }
            mYellowFishPreviousNum = currentYellowFishNum;
        }

        String blankFishNum = sharedPreferences.getString(FishSettingActivity.BLANK_FISH_KEY, DEFAULT_FISH_NUM);
        int currentBlankFishNum = Integer.valueOf(blankFishNum);
        if (mBlankFishPreviousNum != currentBlankFishNum) {
            mBlankWalksFishList.clear();
            for (int i = 0; i < currentBlankFishNum; i++) {
                WalksFishControl blankWalksFish = new WalksFishControl(mContext);
                blankWalksFish.setFishColor(BLANK_FISH_COLOR);
                mBlankWalksFishList.add(blankWalksFish);
            }
            mBlankFishPreviousNum = currentBlankFishNum;
        }

        if (DEBUG) {
            Log.d(TAG, " updateFishNum() 红色鱼的数量: " + mRedWalksFishList.size() + " 黄色鱼的数量: " + mYellowWalksFishList.size()
                    + " 黑色鱼的数量: " + mBlankWalksFishList.size());
        }
    }

    //用于重绘鱼
    private Runnable mDrawTargetRun = new Runnable() {
        @Override
        public void run() {
            drawTarget();
        }
    };

    private Runnable mAdvanceRun = new Runnable() {
        @Override
        public void run() {
            for (WalksFishControl redWalksFish : mRedWalksFishList) {
                updateFishPosition(redWalksFish);
            }
            for (WalksFishControl yellowWalksFish : mYellowWalksFishList) {
                updateFishPosition(yellowWalksFish);
            }
            for (WalksFishControl blankWalksFish : mBlankWalksFishList) {
                updateFishPosition(blankWalksFish);
            }
            mHandler.removeCallbacks(mAdvanceRun);
            mHandler.post(mAdvanceRun);
            mTouchPositionX = -1;
            mTouchPositionY = -1;
        }
    };

    /**
     * 更新鱼的位置
     *
     * @param walksFishControl
     */
    private void updateFishPosition(WalksFishControl walksFishControl) {
        PointF currentPosition = walksFishControl.getCurrentPosition();
        if (currentPosition.x + walksFishControl.getFishHeadRadius() >= mTouchPositionX
                && currentPosition.x - walksFishControl.getFishHeadRadius() <= mTouchPositionX
                && currentPosition.y + walksFishControl.getFishHeadRadius() >= mTouchPositionY
                && currentPosition.y - walksFishControl.getFishHeadRadius() <= mTouchPositionY) {
            walksFishControl.startFishShyAnimator();
        }
        walksFishControl.updateFishPosition();
    }

    /**
     * 用于随机产生鱼的方向
     */
    private Runnable mObtainAngle = new Runnable() {
        @Override
        public void run() {
            for (WalksFishControl redWalksFish : mRedWalksFishList) {
                redWalksFish.updateFishAngle();
            }
            for (WalksFishControl yellowWalksFish : mYellowWalksFishList) {
                yellowWalksFish.updateFishAngle();
            }
            for (WalksFishControl blankWalksFish : mBlankWalksFishList) {
                blankWalksFish.updateFishAngle();
            }
            mHandler.removeCallbacks(mObtainAngle);
            mHandler.postDelayed(mObtainAngle, FISH_OBTAIN_ANGLE_DURATION);
        }
    };

    private void drawTarget() {
        Canvas canvas = null;
        try {
            if (DEBUG) {
                Log.d(TAG, " drawTarget() lockCanvas ");
            }
            canvas = mSurfaceHolder.lockCanvas();
            if (mVisible && canvas != null) {
                canvas.save();
                canvas.drawColor(0xff66ccff);
                for (WalksFishControl firstWalksFish : mRedWalksFishList) {
                    firstWalksFish.drawFish(canvas);
                }
                for (WalksFishControl firstWalksFish : mYellowWalksFishList) {
                    firstWalksFish.drawFish(canvas);
                }
                for (WalksFishControl firstWalksFish : mBlankWalksFishList) {
                    firstWalksFish.drawFish(canvas);
                }
                if (mTouchCircleView != null) {
                    mTouchCircleView.drawCircle(canvas);
                }
                canvas.restore();
            }
        } finally {
            if (canvas != null) {
                if (DEBUG) {
                    Log.d(TAG, " drawTarget() unlockCanvasAndPost ");
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        mHandler.removeCallbacks(mDrawTargetRun);
        mHandler.post(mDrawTargetRun);
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mEnableTouch) {
                    mTouchPositionX = event.getX();
                    mTouchPositionY = event.getY();
                    mTouchCircleView = new TouchCircleView(mContext, this);
                    mTouchCircleView.setTouchPosition(mTouchPositionX, mTouchPositionY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
    }

    public void onDestroyView() {
        mHandler.removeCallbacks(mDrawTargetRun);
        mHandler.removeCallbacks(mObtainAngle);
        mHandler.removeCallbacks(mAdvanceRun);
    }

    @Override
    public void circleDisappear() {
        mTouchCircleView = null;
    }
}
