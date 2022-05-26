package com.jesen.andglidetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.jesen.andglidetalk.R;
public class SecondActivity extends AppCompatActivity {


    /**
     * 1. View 默认会持有一个 Context 的引用，如果将其置为 static 将会造成 View 在方法区中无法被快速回收，最终导致 Activity 内存泄漏。
     * */
    private static ImageView staticViewLeak,broadcastLeak;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("SecondActivity","BroadcastReceiver, ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        staticViewLeak = findViewById(R.id.staticViewLeak);
        staticViewLeak.setImageResource(R.drawable.activity_leak);

        broadcastLeak = findViewById(R.id.broadcastLeak);
        broadcastLeak.setImageResource(R.drawable.broadcast_leak);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 2. 广播，各种listener注册完，需要解注册，否则也会内存泄漏
         * */
        IntentFilter filter = new IntentFilter();
        registerReceiver(receiver,filter);
    }
}