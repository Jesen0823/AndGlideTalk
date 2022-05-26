package com.jesen.andglidetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class ThirdActivity extends AppCompatActivity {

    private ImageView handleLeak;

    private final Handler handler1 = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1){
                Log.d("ThirdActivity","handleMessage what 1");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        handleLeak = findViewById(R.id.handleLeak);

        handler1.sendEmptyMessageDelayed(1,5000);

        Handler2 handler2 = new Handler2();

        for (int i = 0; i < 100; i++) {
            handler2.sendEmptyMessageDelayed(2,1000);
        }
    }

    class Handler2 extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 2){
                Log.d("ThirdActivity","handleMessage what 2");
            }
        }
    }
}