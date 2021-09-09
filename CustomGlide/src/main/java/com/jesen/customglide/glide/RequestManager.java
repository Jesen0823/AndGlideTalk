package com.jesen.customglide.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.jesen.customglide.glide.frag.ActivityFragmentManager;
import com.jesen.customglide.glide.frag.FragmentActivityFragmentManager;

/**
 * 管理生命周期
 */
public class RequestManager {

    private final String FRAGMENT_ACTIVITY_NAME = "fragment_activity_name";
    private final String ACTIVITY_NAME = "activity_name";
    private final int NEXT_HANDLER_MSG = 995465;

    private Context requestManagerContext;
    private static RequestTargetEngine requestTargetEngine;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            return false;
        }
    });

    // 构造代码块
    {
        if (requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }


    /**
     * 可以管理生命周期
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;

        // 拿到Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragment == null) {
            fragment = new FragmentActivityFragmentManager(requestTargetEngine);
            // 添加到管理
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME)
                    .commitAllowingStateLoss();
            mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
        }
    }

    /**
     * 可以管理生命周期
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;
        // 拿到Fragment
        android.app.FragmentManager supportFragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = supportFragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (fragment == null) {
            fragment = new ActivityFragmentManager(requestTargetEngine);
            // 添加到管理
            supportFragmentManager.beginTransaction().add(fragment, ACTIVITY_NAME)
                    .commitAllowingStateLoss();
            // LAUNCH_ACTIVITY 发送一个Handler让Fragment不用在排队中，
            // 否则supportFragmentManager.findFragmentByTag(ACTIVITY_NAME) 会取到空
            mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
        }
    }

    /**
     * 无法管理生命周期
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    public RequestTargetEngine load(String url) {
        // 移除Handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        return requestTargetEngine;
    }
}
