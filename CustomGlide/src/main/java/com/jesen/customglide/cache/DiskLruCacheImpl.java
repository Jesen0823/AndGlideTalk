package com.jesen.customglide.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jesen.customglide.BaseApplication;
import com.jesen.customglide.Tool;
import com.jesen.customglide.disklrucache.DiskLruCache;
import com.jesen.customglide.resource.Value;
import com.jesen.customglide.reusepool.ReusePoolImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存的封装
 */
public class DiskLruCacheImpl {
    private final String TAG = DiskLruCacheImpl.class.getSimpleName();

    private final String DISK_LRU_CACHE_DIR = "lru_cache_dir"; // 磁盘缓存的的目录

    // 版本号，一旦修改这个版本号，之前的缓存失效
    private final int APP_VERSION = 1;
    // 通常情况下都是1
    private final int VALUE_COUNT = 1;

    // 配置缓存大小
    private final long MAX_SIZE = 1024 * 1024 * 100;

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl() {
        File file = new File(BaseApplication.getApplication().getCacheDir() + File.separator + DISK_LRU_CACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);

        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            // index 不能大于 VALUE_COUNT
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            // 把bitmap写入到outputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // 失败
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, "put: editor.abort() e:" + e.getMessage());
            }
        } finally {
            try {
                editor.commit();

                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit(); e:" + e.getMessage());
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "put: outputStream.close(); e:" + e.getMessage());
                }
            }
        }
    }

    public Value get(String key) {
        Tool.checkNotEmpty(key);

        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            // 判断快照不为null的情况下，在去读取操作
            if (null != snapshot) {
                Value value = Value.getInstance();
                // index 不能大于 VALUE_COUNT
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                // 保存key 唯一标识
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close(); e:" + e.getMessage());
                }
            }
        }
        return null; // 为了后续好判断
    }

    public Value get(String key, ReusePoolImpl reusePool) {
        Tool.checkNotEmpty(key);

        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            // 判断快照不为null的情况下，在去读取操作
            if (null != snapshot) {
                Value value = Value.getInstance();
                // index 不能大于 VALUE_COUNT
                inputStream = snapshot.getInputStream(0);
               BitmapFactory.Options options = new BitmapFactory.Options();
                // 只获取图片的周围信息，内置会只获取图片的一部分而已，值获取高宽的信息 outW，outH
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;

                // 使用复用池，拿去复用图片内存
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                Bitmap bitmapPoolResult = reusePool.get(w, h, Bitmap.Config.RGB_565);
                options2.inBitmap = bitmapPoolResult; // 如果我们这里拿到的是null，就不复用
                options2.inMutable = true;
                options2.inPreferredConfig = Bitmap.Config.RGB_565;
                options2.inJustDecodeBounds = false;
                // inSampleSize:是采样率，当inSampleSize为2时，一个2000 1000的图片，将被缩小为1000 500， 采样率为1 代表和原图宽高最接近
                options2.inSampleSize = Tool.sampleBitmapSize(options2, w, h);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2); // 真正的加载

                value.setmBitmap(bitmap);
                // 保存key 唯一标识
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close(); e:" + e.getMessage());
                }
            }
        }
        return null;
    }
}
