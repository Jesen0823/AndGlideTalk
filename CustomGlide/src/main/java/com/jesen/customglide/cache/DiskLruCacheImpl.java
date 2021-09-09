package com.jesen.customglide.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.jesen.customglide.Tool;
import com.jesen.customglide.disklrucache.DiskLruCache;
import com.jesen.customglide.resource.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存的封装
 */
public class DiskLruCacheImpl {

    private static final String TAG = "DiskLruCacheImpl";
    private static final String DISK_LRU_CACHE_DIR = "lru_cache_dir";
    private static String PATH = "";

    // 缓存的版本号，修改后原有缓存失效
    private final int CACHE_VERSION = 100;
    private final int VALUE_COUNT = 1;
    private final long MAX_SIZE = 1024 * 1024 * 10;


    private DiskLruCache diskLruCache;
    private Context mContext;

    public DiskLruCacheImpl(Context context) {
        mContext = context;
        PATH = context.getCacheDir().getAbsolutePath();
        String cachePath = PATH + File.separator + DISK_LRU_CACHE_DIR;

        File file = new File(cachePath);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            diskLruCache = DiskLruCache.open(file, CACHE_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Value value) {
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            // index不能大于 VALUE_COUNT
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            // 将Bitmap写入 outputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.w(TAG, "put failed.err: " + e.toString());
            }
        } finally {
            try {
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, "commit failed.err: " + e.toString());
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Value get(String key) {
        Tool.checkNotEmpty(key);

        Value value = Value.getInstance();
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                // 保存key
                value.setKey(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
