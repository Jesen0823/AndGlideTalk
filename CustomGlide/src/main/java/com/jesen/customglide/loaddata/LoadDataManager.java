package com.jesen.customglide.loaddata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.jesen.customglide.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 加载外部资源实现
 */
public class LoadDataManager implements ILoadData, Runnable {
    private static final String TAG = "LoadDataManager";

    private String path;
    private LoadResponseListener responseListener;
    private ExecutorService executorService;

    @Override
    public void loadResource(String path, LoadResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;

        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            executorService = Executors.newCachedThreadPool(); // 单任务线程池
            executorService.execute(this);
        }
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        Bitmap bitmap = null;

        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }

            // 回调到主线程
            final Bitmap finalBitmap = bitmap;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (finalBitmap != null) {
                        Value value = Value.getInstance();
                        value.setmBitmap(finalBitmap);
                        responseListener.responseSuccess(value);
                    } else {
                        responseListener.responseException(
                                new IllegalStateException("load net resource failed, code:"+ responseCode));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }
}
