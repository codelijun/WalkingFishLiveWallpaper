package com.example.lijun.walkingfishwallpaperdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lijun.walkingfishwallpaperdemo.BuildConfig;
import com.example.lijun.walkingfishwallpaperdemo.util.ScreenUtil;

public class WalksFishView {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "WalksFishView";
    private static final int FISH_HEAD_RADIUS_DP = 20;
    private static final int FISH_TAIL_INTERVAL_DP = 6;
    private static final int FISH_FINS_RIGHT = 0;
    private static final int FISH_FINS_LEFT = 1;

    private Paint mPaint;
    private Context mContext;
    //动画引擎控制的动态改变的值
    private int mCurrentValue;
    //坐标系的中点坐标
    private PointF mMiddlePoint;
    //鱼头的坐标
    private PointF mHeadPoint;
    private int mFishHeadRadius;

    //鱼身体部位的坐标
    private PointF mBodyEndPoint;
    private int mFishBodyLength;

    //腰部由两部分组成
    private PointF mPrimaryWaistEndPoint;
    private PointF mSecondaryWaistEndPoint;
    private int mPrimaryWaistLength;
    private int mSecondaryWaistLength;
    private int mPrimaryCircleRadius;
    private int mSecondaryCircleRadius;

    //鱼鳍开始的坐标
    private PointF mFishFinsStartPoint;
    private int mFishFinsLength;

    private float mMainAngle; // 鱼身体和X轴正方向的夹角,控制鱼整体的方向
    private float mDynamicAngle; //受动画引擎的控制,影响鱼摆动的角度
    private int mWaveFrequence; // 全局的频率,控制鱼摆动的频率,只能是整数
    private Path mPath;

    //鱼尾的长度
    private int mFishTailLength;
    //鱼尾张开的最大宽度
    private int mFishTailMaxWidth;
    private final int mFishTailInterval;

    public WalksFishView(Context context) {
        this.mContext = context;
        this.mPaint = new Paint();
        this.mPath = new Path();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(0xffFF4500);
        this.mPaint.setStrokeWidth(2f);

        mMainAngle = (float) (Math.random() * 360); //初始化鱼,默认的角度
        mWaveFrequence = 1;
        mFishHeadRadius = ScreenUtil.dip2px(mContext, FISH_HEAD_RADIUS_DP);
        mFishTailInterval = ScreenUtil.dip2px(mContext, FISH_TAIL_INTERVAL_DP);
        mMiddlePoint = new PointF(getIntrinsicWidth(), getIntrinsicHeight());

        mFishBodyLength = (int) (mFishHeadRadius * 3.2f);
        mFishFinsLength = (int) (mFishBodyLength * 0.3f);


        mPrimaryCircleRadius = (int) (mFishHeadRadius * 0.7f);
        mSecondaryCircleRadius = (int) (mPrimaryCircleRadius * 0.6f);
        mPrimaryWaistLength = mPrimaryCircleRadius + mSecondaryCircleRadius;
        mSecondaryWaistLength = mPrimaryWaistLength;

        mFishTailLength = (int) (mFishHeadRadius * 1.2f);
        mFishTailMaxWidth = (int) (mPrimaryCircleRadius * 0.6f);
    }

    public void setCurrentValue(int currentValue) {
        this.mCurrentValue = currentValue;
    }

    public void setMainAngle(float mainAngle) {
        this.mMainAngle = mainAngle;
    }

    public void setFishColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setmWaveFrequence(int mWaveFrequence) {
        this.mWaveFrequence = mWaveFrequence;
    }

    public float getMainAngle() {
        return mMainAngle;
    }

    public int getFishHeadRadius() {
        return mFishHeadRadius;
    }

    public int getIntrinsicWidth() {
        return (int) (8.4f * mFishHeadRadius);
    }

    public int getIntrinsicHeight() {
        return (int) (8.4f * mFishHeadRadius);
    }

    public void setFishAge(int age) {
        mFishHeadRadius = (int) (mFishHeadRadius * 0.5f) * age;
        mMiddlePoint = new PointF(getIntrinsicWidth(), getIntrinsicHeight());

        mFishBodyLength = (int) (mFishHeadRadius * 3.2f);
        mFishFinsLength = (int) (mFishBodyLength * 0.3f);


        mPrimaryCircleRadius = (int) (mFishHeadRadius * 0.7f);
        mSecondaryCircleRadius = (int) (mPrimaryCircleRadius * 0.6f);
        mPrimaryWaistLength = mPrimaryCircleRadius + mSecondaryCircleRadius;
        mSecondaryWaistLength = mPrimaryWaistLength;

        mFishTailLength = (int) (mFishHeadRadius * 1.2f);
        mFishTailMaxWidth = (int) (mPrimaryCircleRadius * 0.6f);
    }

    public void setFishCurrentPosition(float positionX, float positionY) {
        this.mMiddlePoint.x = positionX;
        this.mMiddlePoint.y = positionY;
    }

    public void drawFish(@NonNull Canvas canvas) {
        if (DEBUG) {
            Log.d(TAG, " draw() ");
        }
        mDynamicAngle = (float) (Math.sin(Math.toRadians(mCurrentValue * mWaveFrequence)) * 2f);//生成[-2,2]区间的角度
        drawFishHead(canvas);
        drawFishBody(canvas);
        drawFishFins(canvas, FISH_FINS_LEFT);
        drawFishFins(canvas, FISH_FINS_RIGHT);
        drawFishWaist(canvas);
        drawFishTail(canvas);
    }

    private void drawFishHead(Canvas canvas) {
        mHeadPoint = calculatPoint(mMiddlePoint, mFishBodyLength * 0.5f, mMainAngle);
        mPaint.setAlpha(222);
        canvas.drawCircle(mHeadPoint.x, mHeadPoint.y, mFishHeadRadius, mPaint);
    }

    private void drawFishBody(Canvas canvas) {
        float dynamicAngle = mDynamicAngle + mMainAngle;
        mBodyEndPoint = calculatPoint(mMiddlePoint, mFishBodyLength * 0.5f, dynamicAngle - 180);
        PointF point1, point2, point3, point4, contralLeft, contralRight;
        //point1和4的初始角度决定发髻线的高低值越大越低
        point1 = calculatPoint(mHeadPoint, mFishHeadRadius, dynamicAngle - 80);
        point2 = calculatPoint(mBodyEndPoint, mPrimaryCircleRadius, dynamicAngle - 90);
        point3 = calculatPoint(mBodyEndPoint, mPrimaryCircleRadius, dynamicAngle + 90);
        point4 = calculatPoint(mHeadPoint, mFishHeadRadius, dynamicAngle + 80);
        //决定胖瘦
        contralLeft = calculatPoint(mHeadPoint, mFishBodyLength * 0.56f, dynamicAngle - 130);
        contralRight = calculatPoint(mHeadPoint, mFishBodyLength * 0.56f, dynamicAngle + 130);
        mPath.reset();
        mPath.moveTo(point1.x, point1.y);
        mPath.quadTo(contralLeft.x, contralLeft.y, point2.x, point2.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.quadTo(contralRight.x, contralRight.y, point4.x, point4.y);
        mPath.lineTo(point1.x, point1.y);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawFishFins(Canvas canvas, int type) {
        float dynamicAngle = mDynamicAngle + mMainAngle;

        float finsAngle = (float) (Math.cos(Math.toRadians(mCurrentValue * mWaveFrequence - 180)) * 15f + 15f);
        mFishFinsStartPoint = calculatPoint(mHeadPoint, mFishHeadRadius * 0.9f, type == FISH_FINS_RIGHT ? dynamicAngle - 90 : dynamicAngle + 90);
        //鱼鳍控制点相对于鱼主轴方向的角度
        float contralAngle = 115;
        mPath.reset();
        mPath.moveTo(mFishFinsStartPoint.x, mFishFinsStartPoint.y);
        //鱼鳍的另一端
        PointF endPoint = calculatPoint(mFishFinsStartPoint, mFishFinsLength, type == FISH_FINS_RIGHT ? dynamicAngle - 180 : dynamicAngle + 180);
        //曲线的控制点
        PointF contralPoint = calculatPoint(mFishFinsStartPoint, mFishFinsLength * 1.8f, type == FISH_FINS_RIGHT ?
                dynamicAngle - contralAngle - finsAngle : dynamicAngle + contralAngle + finsAngle);
        mPath.quadTo(contralPoint.x, contralPoint.y, endPoint.x, endPoint.y);
        mPath.lineTo(mFishFinsStartPoint.x, mFishFinsStartPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawFishWaist(Canvas canvas) {
//        mPaint.setAlpha(150);
        canvas.drawCircle(mBodyEndPoint.x, mBodyEndPoint.y, mPrimaryCircleRadius, mPaint);

        float PrimaryDynamicAngle = mMainAngle + mDynamicAngle * 8;
        mPrimaryWaistEndPoint = calculatPoint(mBodyEndPoint, mPrimaryWaistLength, PrimaryDynamicAngle - 180);
        PointF point1, point2, point3, point4;
        point1 = calculatPoint(mBodyEndPoint, mPrimaryCircleRadius, PrimaryDynamicAngle - 90);
        point2 = calculatPoint(mBodyEndPoint, mPrimaryCircleRadius, PrimaryDynamicAngle + 90);
        point3 = calculatPoint(mPrimaryWaistEndPoint, mSecondaryCircleRadius, PrimaryDynamicAngle + 90);
        point4 = calculatPoint(mPrimaryWaistEndPoint, mSecondaryCircleRadius, PrimaryDynamicAngle - 90);
        mPath.reset();
        mPath.moveTo(point1.x, point1.y);
        mPath.lineTo(point2.x, point2.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.lineTo(point4.x, point4.y);
        canvas.drawPath(mPath, mPaint);

        canvas.drawCircle(mPrimaryWaistEndPoint.x, mPrimaryWaistEndPoint.y, mSecondaryCircleRadius, mPaint);

        float secondaryDynamicAngle = mMainAngle + mDynamicAngle * 20;
        mSecondaryWaistEndPoint = calculatPoint(mPrimaryWaistEndPoint, mSecondaryWaistLength, secondaryDynamicAngle - 180);
        PointF secondaryPoint1, secondaryPoint2, secondaryPoint3, secondaryPoint4;
        secondaryPoint1 = calculatPoint(mPrimaryWaistEndPoint, mSecondaryCircleRadius, secondaryDynamicAngle - 90);
        secondaryPoint2 = calculatPoint(mPrimaryWaistEndPoint, mSecondaryCircleRadius, secondaryDynamicAngle + 90);
        secondaryPoint3 = calculatPoint(mSecondaryWaistEndPoint, mSecondaryCircleRadius * 0.5f, secondaryDynamicAngle + 90);
        secondaryPoint4 = calculatPoint(mSecondaryWaistEndPoint, mSecondaryCircleRadius * 0.5f, secondaryDynamicAngle - 90);
        mPath.reset();
        mPath.moveTo(secondaryPoint1.x, secondaryPoint1.y);
        mPath.lineTo(secondaryPoint2.x, secondaryPoint2.y);
        mPath.lineTo(secondaryPoint3.x, secondaryPoint3.y);
        mPath.lineTo(secondaryPoint4.x, secondaryPoint4.y);
        canvas.drawPath(mPath, mPaint);

        canvas.drawCircle(mSecondaryWaistEndPoint.x, mSecondaryWaistEndPoint.y, mSecondaryCircleRadius * 0.5f, mPaint);
    }


    private void drawFishTail(Canvas canvas) {
        float dynamicAngle = mMainAngle + mDynamicAngle * 20;
        float newWidth = (float) Math.abs(Math.sin(Math.toRadians(mCurrentValue * 2f * mWaveFrequence)) * mFishTailMaxWidth + mFishHeadRadius / 5 * 3);
        //endPoint为三角形底边中点
        PointF endPoint = calculatPoint(mPrimaryWaistEndPoint, mFishTailLength, dynamicAngle - 180);
        PointF endPoint2 = calculatPoint(mPrimaryWaistEndPoint, mFishTailLength - mFishTailInterval, dynamicAngle - 180);
        PointF point1, point2, point3, point4;
        point1 = calculatPoint(endPoint, newWidth, dynamicAngle - 90);
        point2 = calculatPoint(endPoint, newWidth, dynamicAngle + 90);
        point3 = calculatPoint(endPoint2, newWidth - mFishTailInterval, dynamicAngle - 90);
        point4 = calculatPoint(endPoint2, newWidth - mFishTailInterval, dynamicAngle + 90);
        //内
        mPaint.setAlpha(130);
        mPath.reset();
        mPath.moveTo(mPrimaryWaistEndPoint.x, mPrimaryWaistEndPoint.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.lineTo(point4.x, point4.y);
        mPath.lineTo(mPrimaryWaistEndPoint.x, mPrimaryWaistEndPoint.y);
        canvas.drawPath(mPath, mPaint);
        //外
        mPath.reset();
        mPath.moveTo(mPrimaryWaistEndPoint.x, mPrimaryWaistEndPoint.y);
        mPath.lineTo(point1.x, point1.y);
        mPath.lineTo(point2.x, point2.y);
        mPath.lineTo(mPrimaryWaistEndPoint.x, mPrimaryWaistEndPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 输入起点、长度、旋转角度计算终点
     *
     * @param startPoint 起点
     * @param length     长度
     * @param angle      旋转角度
     * @return 终点
     */
    private static PointF calculatPoint(PointF startPoint, float length, float angle) {
        float deltaX = (float) Math.cos(Math.toRadians(angle)) * length;
        //符合Android坐标的y轴朝下的标准
        float deltaY = (float) Math.sin(Math.toRadians(angle - 180)) * length;
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }
}
