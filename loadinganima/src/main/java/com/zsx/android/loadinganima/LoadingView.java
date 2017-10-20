package com.zsx.android.loadinganima;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * 创建者： feifan.pi 在 2017/10/20.
 */

public class LoadingView extends View {
    //背景颜色
    private final int VIEW_BG_COLOR = Color.WHITE;
    //旋转动画时间
    private final long ROTATION_ANIMATOR_TIME = 2000;
    //旋转动画时间
    private final long Meger_ANIMATOR_TIME = 1000;
    //当前大圆形旋转弧度
    private float mCurrentRotationAngle = 0f;
    //小圆点的颜色列表
    private int[] mCircleColors;
    //大圆包含很多小圆的半径  整体宽度的1/6
    private float mRotationRadius;
    //小圆的半径 大圆半径的1/8
    private float mCircleRadius;
    //画笔
    private Paint paint;
    //大圆中心点
    private int mCenterX, mCenterY;
    //当前状态画的动画
    private LoadingState mlLoadingState;
    //当前大圆半径
    private float mCurrentRatationRadius = mRotationRadius;
    //空心圆初始半径
    private float mHoleRadius = 0f;
    //屏幕对角线一般
    private float mDiagonalDist;


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取小圆点的颜色列表
        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);
    }

    private boolean mInitParams = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mInitParams) {
            initParams();
        }
        if (null == mlLoadingState) {
            mlLoadingState = new RotationState();
        }
        //让圆形旋转
        mlLoadingState.draw(canvas);
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        //大圆半径
        mRotationRadius = getMeasuredWidth() / 6;
        //小圆半径
        mCircleRadius = mRotationRadius / 8;
        mInitParams = true;

        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);

        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;

        //屏幕对角线
        mDiagonalDist = (float) Math.sqrt(mCenterX * mCenterX + mCenterY * mCenterY);

    }


    /**
     * 消失动画
     */
    public void disapper() {
        //关闭旋转动画
        if (mlLoadingState instanceof RotationState) {
            RotationState rotationState = (RotationState) mlLoadingState;
            rotationState.cancle();
        }
        //开始聚合动画
        mlLoadingState = new MergeState();
    }

    //监听当前动画
    public abstract class LoadingState {
        public abstract void draw(Canvas canvas);
    }

    /**
     * 旋转动画
     */
    public class RotationState extends LoadingState {
        //动画控制
        private ValueAnimator mAnimator;

        public RotationState() {
            if (null == mAnimator) {
                //旋转度数 0-360
                mAnimator = ObjectAnimator.ofFloat(0f, 2 * (float) Math.PI);
                mAnimator.setDuration(ROTATION_ANIMATOR_TIME);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mCurrentRotationAngle = (float) valueAnimator.getAnimatedValue();
                        //重新绘制
                        invalidate();
                    }
                });
                //线性差值器
                mAnimator.setInterpolator(new LinearInterpolator());
                //重复执行
                mAnimator.setRepeatCount(-1);
                mAnimator.start();
            }
        }

        /**
         * 取消动画
         */
        public void cancle() {
            mAnimator.cancel();
        }

        @Override
        public void draw(Canvas canvas) {
            //画个白色背景
            canvas.drawColor(VIEW_BG_COLOR);
            //绘制6个小圆点 并分配每个园的初始角度
            double percentAngle = Math.PI * 2 / mCircleColors.length;
            for (int i = 0; i < mCircleColors.length; i++) {
                paint.setColor(mCircleColors[i]);
                //当前角度=初始角度+旋转角度
                double currentAngle = percentAngle * i + mCurrentRotationAngle;
                int cx = (int) (mCenterX + mRotationRadius * Math.cos(currentAngle));
                int cy = (int) (mCenterY + mRotationRadius * Math.sin(currentAngle));
                canvas.drawCircle(cx, cy, mCircleRadius, paint);
            }
        }
    }


    /**
     * 聚合动画
     */
    public class MergeState extends LoadingState {
        //动画控制
        private ValueAnimator mAnimator;

        public MergeState() {
            mAnimator = ObjectAnimator.ofFloat(mRotationRadius, 0);
            mAnimator.setDuration(Meger_ANIMATOR_TIME);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mCurrentRatationRadius = (float) valueAnimator.getAnimatedValue();
                    //重新绘制
                    invalidate();
                }
            });
            mAnimator.setInterpolator(new AnticipateInterpolator(3f));
            mAnimator.addListener(new AnimatorListenerAdapter() {
                                      @Override
                                      public void onAnimationEnd(Animator animation) {
                                          //聚合动画完成后开始扩展动画
                                          mlLoadingState = new ExpentState();
                                      }
                                  }
            );
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            //画个白色背景
            canvas.drawColor(VIEW_BG_COLOR);
            //绘制6个小圆点 并分配每个园的初始角度
            double percentAngle = Math.PI * 2 / mCircleColors.length;
            for (int i = 0; i < mCircleColors.length; i++) {
                paint.setColor(mCircleColors[i]);
                //当前角度=初始角度+旋转角度
                double currentAngle = percentAngle * i + mCurrentRotationAngle;
                int cx = (int) (mCenterX + mCurrentRatationRadius * Math.cos(currentAngle));
                int cy = (int) (mCenterY + mCurrentRatationRadius * Math.sin(currentAngle));
                canvas.drawCircle(cx, cy, mCircleRadius, paint);
            }
        }
    }

    /**
     * 展开动画
     */
    public class ExpentState extends LoadingState {
        //动画控制
        private ValueAnimator mAnimator;

        public ExpentState() {
            mAnimator = ObjectAnimator.ofFloat(0, mDiagonalDist);
            mAnimator.setDuration(Meger_ANIMATOR_TIME / 2);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mHoleRadius = (float) valueAnimator.getAnimatedValue();
                    //重新绘制
                    invalidate();
                }
            });
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.start();
        }

        private int color = -1;

        @Override
        public void draw(Canvas canvas) {
            //画笔的宽度
            float strokeWidth = mDiagonalDist - mHoleRadius;
            paint.setStrokeWidth(strokeWidth);
            if (color == -1) {
                //动态随机扩展波纹色
                int colorId = new Random().nextInt(mCircleColors.length);
                color = mCircleColors[colorId];
            }
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            float radius = strokeWidth / 2 + mHoleRadius;
            canvas.drawCircle(mCenterX, mCenterY, radius, paint);
        }
    }
}
