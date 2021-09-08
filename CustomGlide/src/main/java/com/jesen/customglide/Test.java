package com.jesen.customglide;

import android.util.Log;

import com.jesen.customglide.cache.MemoryCache;
import com.jesen.customglide.cache.MemoryCacheCallback;
import com.jesen.customglide.resource.Value;


public class Test {

    private static final String TAG = "Test";

    // 伪代码
    public void test() {

        MemoryCache memoryCache = new MemoryCache(5);
        memoryCache.put("aasfsdfsdf", new Value());

        final Value v = memoryCache.get("aasfsdfsdf");

        memoryCache.shoudonRemove("aasfsdfsdf");

        memoryCache.setMemoryCacheCallback(new MemoryCacheCallback() {
            @Override
            public void entryRemovedMemoryCache(String key, Value oldValue) {
                Log.d(TAG, "entryRemovedMemoryCache: 内存缓存中的元素被移除了【被动移除】 value:" + oldValue + " key:" + key);
            }
        });
    }


}
