package com.jesen.customglide.glide;

import android.util.Log;
import android.widget.ImageView;

/**
 * 真正的加载资源负责类
 * */
public class RequestTargetEngine implements LifeCycleCallback{

    private static final String TAG = "RequestTargetEngine";

    @Override
    public void glideInitEvent() {
        Log.d(TAG,"---glideInitEvent");

    }

    @Override
    public void glideStopEvent() {
        Log.d(TAG,"---glideStopEvent");
    }

    @Override
    public void glideRecycleEvent() {
        Log.d(TAG,"---glideRecycleEvent");
    }

    public void into(ImageView view) {

    }
}
