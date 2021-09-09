package com.jesen.customglide.glide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.jesen.customglide.Tool;
import com.jesen.customglide.cache.ActiveCache;
import com.jesen.customglide.cache.DiskLruCacheImpl;
import com.jesen.customglide.cache.MemoryCache;
import com.jesen.customglide.cache.MemoryCacheCallback;
import com.jesen.customglide.loaddata.LoadDataManager;
import com.jesen.customglide.loaddata.LoadResponseListener;
import com.jesen.customglide.resource.Key;
import com.jesen.customglide.resource.Value;
import com.jesen.customglide.resource.ValueCallback;

/**
 * 真正的加载资源负责类
 * */
public class RequestTargetEngine implements LifeCycleCallback, ValueCallback, MemoryCacheCallback ,
        LoadResponseListener {

    private static final String TAG = "RequestTargetEngine";
    // 内存最大缓存量
    private static final int MEMORY_MAX_SIZE = 1024 * 1024 * 30;

    // 活动缓存
    private ActiveCache activeCache;

    // 内存缓存
    private MemoryCache memoryCache;

    // 磁盘缓存
    private DiskLruCacheImpl diskLruCacheImpl;

    private String imgPath;
    private Context glideContext;
    private String key;
    private ImageView imageView;

    public RequestTargetEngine(){
        if (activeCache == null){
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null){
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);
        }

        diskLruCacheImpl = new DiskLruCacheImpl();
    }

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
        if (activeCache != null){
            activeCache.closeThread();
        }
    }



    // 由RequestManage传递
    public void loadValueInit(String imgPath, Context glideContext){
        this.glideContext = glideContext;
        this.imgPath = imgPath;
        key = new Key(imgPath).getKey();
    }

    public void into(ImageView view) {
        this.imageView = view;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        Value value = checkCache();
        if (value != null){
            // 使用完计数减1
            value.nonUseAction();
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    private Value checkCache() {
        Value value = activeCache.get(key);
        if (value != null){
            Log.d(TAG, "--checkCache, activeCache is find, key:"+key);
            value.useAction(); // 使用一次标记增加1
            return value;
        }

        // 去内存寻找,加入活动缓存
        value = memoryCache.get(key);
        if(value != null){
            Log.d(TAG, "--checkCache, memoryCache is find, key:"+key);
            memoryCache.shoudonRemove(key);
            activeCache.put(key,value);
            value.useAction();
            return value;
        }

        // 磁盘查找
        value = diskLruCacheImpl.get(key);
        if (value != null){
            Log.d(TAG, "--checkCache, diskLruCacheImpl is find, key:"+key);
            activeCache.put(key,value);
            //memoryCache.put(key,value);

            value.useAction();
            return value;
        }

        // 加载网络资源
        new LoadDataManager().loadResource(imgPath, this, glideContext);

        return null;
    }

    /**
     * 活动缓存间接调用Value
     * */
    @Override
    public void valueNonUseListener(String key, Value value) {
        Log.d(TAG,"--valueNonUseListener");
        // 活动缓存加入内存缓存
        if (key != null && value!= null){
            memoryCache.put(key, value);
        }
    }

    @Override
    public void entryRemovedMemoryCache(String key, Value oldValue) {
        Log.d(TAG,"--entryRemovedMemoryCache");
        // 添加到复用池
    }

    /**
     * 网络图片加载成功
     * */
    @Override
    public void responseSuccess(Value value) {
        Log.d(TAG,"--responseSuccess");
        if(value != null){
            saveCache(key,value);
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    /**
     * 网络图片加载异常
     * */
    @Override
    public void responseException(Exception e) {
        Log.d(TAG,"--responseException, err:"+e.getMessage());
    }

    private void saveCache(String key,Value value){
        Log.d(TAG,"--saveCache, key:"+key);
        value.setKey(key);

        // 保存到磁盘
        if (diskLruCacheImpl != null){
            diskLruCacheImpl.put(key,value);
        }
    }
}
