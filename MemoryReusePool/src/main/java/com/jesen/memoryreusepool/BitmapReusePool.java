package com.jesen.memoryreusepool;

import android.graphics.Bitmap;

/**
 * 复用池标准
 * */
public interface BitmapReusePool {

    /**
     * 存入复用池
     * */
    void put(Bitmap bitmap);


    /**
     * 获取可以复用的Bitmap
     * */
    Bitmap get(int width, int height, Bitmap.Config config);
}
