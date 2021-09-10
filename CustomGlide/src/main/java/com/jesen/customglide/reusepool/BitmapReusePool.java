package com.jesen.customglide.reusepool;

import android.graphics.Bitmap;

/**
 * 复用池标准
 * */
public interface BitmapReusePool {

    /**
     * 存入内存复用池
     * */
    void put(Bitmap bitmap);


    /**
     * 从复用池取出Bitmap
     * */
    Bitmap get(int width, int height, Bitmap.Config config);
}
