package com.jesen.customglide.cache;

import com.jesen.customglide.resource.Value;

/**
 * 内存缓存中，元素被移除的接口回调
 */
public interface MemoryCacheCallback {

    /**
     * 内存缓存中移除的 key--value
     * @param key
     * @param oldValue
     */
    public void entryRemovedMemoryCache(String key, Value oldValue);

}
