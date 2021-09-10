package com.jesen.memoryreusepool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

public class ReusePoolImpl extends LruCache<Integer, Bitmap> implements BitmapReusePool {

    private static final String TAG = "ReusePoolImpl";

    // Bitmap容器，筛选出可以复用的Bitmap
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    public ReusePoolImpl(int maxSize) {
        super(maxSize);
    }

    /**
     * 存入复用池
     * */
    @Override
    public void put(Bitmap bitmap) {

        // 条件1： bitmap.isMutable() == true
        if(!bitmap.isMutable()){
            Log.d(TAG,"---条件1， bitmap.isMutable() == true 不满足");
            return;
        }
        // 条件2：bitmap大小要小于maxSize
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize >= maxSize()){
            Log.d(TAG,"---条件2，不满足 bitmap大小超过了 maxSize");
            return;
        }

        // 存入 LruCache
        put(bitmapSize, bitmap);

        // 存入筛选容器,参数2用不到
        treeMap.put(bitmapSize, null);
        Log.d(TAG,"---put 加入复用池");
    }

    /**
     * 获取可用Bitmap
     * */
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        /**
         * ALPHA_8  理论上 实际上Android自动做处理的 只有透明度 8位  1个字节
         * w*h*1
         *
         * RGB_565  理论上 实际上Android自动做处理的  R red红色 5， G绿色 6， B蓝色 5   16位 2个字节 没有透明度
         * w*h*2
         *
         * ARGB_4444 理论上 实际上Android自动做处理 A透明度 4位  R red红色4位   16位 2个字节
         *
         * 质量最高的：
         * ARGB_8888 理论上 实际上Android自动做处理  A 8位 1个字节  ，R 8位 1个字节， G 8位 1个字节， B 8位 1个字节
         *
         * 常用的 ARGB_8888  RGB_565
         */
        // 常用的 ARGB_8888: 四八32,4字节  RGB_565： 2字节
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        // 可以查找到容器里面 和getSize一样大的元素，或比getSize还要大的
        Integer key = treeMap.ceilingKey(getSize);
        // 如果treeMap 还没有put，那么一定是 null
        if (key == null) {
            // 没有找到合适的 可以复用的 key
            return null;
        }

        Bitmap remove = remove(key);// 复用池 如果要取出来，肯定要移除，不想给其他地方用了
        Log.d(TAG, "get: 从复用池 里面获取了Bitmap...");
        return remove;
    }

    /**
     * 计算Bitmap的大小
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {
        // 最早期的时候 getRowBytes() * getHeight();

        // Android 3.0 12 API  bitmap.getByteCount()
        // bitmap.getByteCount()

        // Android 4.4 19 API 以后的版本
        // bitmap.getAllocationByteCount();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
     * 元素大小
     * */
    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return getBitmapSize(value);
    }

    /**
     * 元素移除
     * */
    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
