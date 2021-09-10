package com.jesen.biglongimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private static final String TAG = "BigView---";

    private final GestureDetector mGestureDetector;
    private final Scroller mScroller;
    private Rect mRect;
    private BitmapFactory.Options mOptions;
    private int mImgWidth;
    private int mImgHeight;
    private BitmapRegionDecoder mRegionDecoder;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private Bitmap mBitmap;
    private Paint mPaint;

    public BigView(Context context) {
        this(context, null);
    }

    public BigView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 设置成员变量属性
        mRect = new Rect();
        // 用来复用内存
        mOptions = new BitmapFactory.Options();
        // 手势识别
        mGestureDetector = new GestureDetector(context, this);
        // 滚动相关
        mScroller = new Scroller(context);

        mPaint = new Paint();

        setOnTouchListener(this);
    }

    // 设置图片
    public void setImage(InputStream is){
        // 获取图片宽高
        mOptions.inJustDecodeBounds = true;
        mBitmap = BitmapFactory.decodeStream(is, null, mOptions);
        mImgWidth = mOptions.outWidth;
        mImgHeight = mOptions.outHeight;
        Log.d(TAG, String.format("setImage, mImgWidth x mImgHeight= %d x %d", mImgWidth,mImgHeight));

        // 开启复用
        mOptions.inMutable = true;
        // 设置格式RGB-565
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        // int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        mOptions.inJustDecodeBounds = false;

        // 区域解码器
        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    // 开始测量View的宽高比，缩放图片资源
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        // 确定加载图片的区域
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImgWidth;
        // 计算缩放比例
        mScale = mViewWidth/(float)mImgWidth;
        Log.d(TAG,"onMeasure, mScale = "+mScale);
        mRect.bottom = (int) (mViewHeight/mScale);
    }

    // 绘制内容
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRegionDecoder == null){
            return;
        }
        // 内存复用
        mOptions.inBitmap = mBitmap;
        // 指定解码区域
        mBitmap = mRegionDecoder.decodeRegion(mRect, mOptions);
        // 缩放矩阵
        Matrix matrix = new Matrix();
        matrix.setScale(mScale,mScale);
        canvas.drawBitmap(mBitmap, matrix,mPaint);

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        // 如果移动没有停止，则强行停止
        if (!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        // 继续接收后续事件
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    /**
     *  event1:开始事件，手指按下
     *  event2：当前事件
     *  distanceX，distanceY xy轴移动的距离
     * */
    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        // 上下滑动时改变显示区域
        mRect.offset(0, (int) distanceY);
        // 滑动到了底部
        if(mRect.bottom > mImgHeight){
            mRect.bottom = mImgHeight;
            mRect.top = (int) (mImgHeight - (mViewHeight/mScale));
        }
        if(mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int)(mViewHeight/mScale);
        }
        invalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    /**
     * 处理滑动惯性
     * */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        // maxY，y方向可滑动最大距离，= 图片高 - view的高
        mScroller.fling(0,mRect.top, 0, -(int) velocityY,0,0,0,
                mImgHeight-(int)(mViewHeight/mScale));
        return false;
    }

    /**
     * 处理计算结果
     * */
    @Override
    public void computeScroll() {
        // 滑动结束
        if(mScroller.isFinished()){
            return;
        }
        // 未结束
        if (mScroller.computeScrollOffset()){
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top + (int) (mViewHeight/mScale);
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // 交给手势处理
        return mGestureDetector.onTouchEvent(motionEvent);
    }
}
