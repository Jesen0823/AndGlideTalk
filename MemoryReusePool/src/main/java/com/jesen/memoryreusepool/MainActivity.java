package com.jesen.memoryreusepool;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable {

    private Button testBtn;
    private ImageView ivTest;

    private final String IMG_URL = "https://tse1-mm.cn.bing.net/th/id/R-C.1496e0b4a2d17b25037cc75ca8328ab0?rik=pGo4LcnY18QrNQ&riu=http%3a%2f%2fpic.lvmama.com%2fuploads%2fpc%2fplace2%2f2016-05-26%2f2732e04c-ffae-49a2-9fbb-72570376cb21.jpg&ehk=x%2baL9ho8oG33wcAqFwq491IpDLG%2flWEETsDegLpn3pI%3d&risl=&pid=ImgRaw&r=0";

    private BitmapReusePool bitmapPool = new ReusePoolImpl(1024*1024*500);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBtn = findViewById(R.id.testBtn);
        ivTest = findViewById(R.id.iv);

        testBtn.setOnClickListener(view -> testRefuse());
    }

    private void testRefuse() {
        new Thread(this).start();

    }

    @Override
    public void run() {
        try {
            URL url = new URL(IMG_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStream inputStream = connection.getInputStream();

                // 拿到图片宽和高
                /*BitmapFactory.Options options = new BitmapFactory.Options();
                // 只拿到周围信息，outXXX， outW，outH
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;*/

                int w = 2696;
                int h = 1799;

                BitmapFactory.Options options2 = new BitmapFactory.Options();

                // 拿到复用池  条件： bitmap.isMutable() == true;
                Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);

                // 如果设置为null，内部就不会去申请新的内存空间，无法复用，依然会重复申请内存，造成：内存抖动，内存碎片
                options2.inBitmap = bitmapPoolResult; // 把复用池的Bitmap 给 inBitmap
                options2.inPreferredConfig = Bitmap.Config.RGB_565; // 2个字节
                options2.inJustDecodeBounds = false;
                options2.inMutable = true; // 符合 复用机制
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2); // 复用内存

                // 添加到复用池
                bitmapPool.put(bitmap);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ivTest.setImageBitmap(bitmap);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}