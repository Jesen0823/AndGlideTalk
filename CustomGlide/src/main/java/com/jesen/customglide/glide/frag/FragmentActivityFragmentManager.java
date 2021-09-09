package com.jesen.customglide.glide.frag;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import com.jesen.customglide.glide.LifeCycleCallback;

/**
 * FragmentActivity生命周期关联管理
 */
public class FragmentActivityFragmentManager extends Fragment {
    private LifeCycleCallback lifeCycleCallback;

    public FragmentActivityFragmentManager() {

    }

    @SuppressLint("ValidFragment")
    public FragmentActivityFragmentManager(LifeCycleCallback callback) {
        this.lifeCycleCallback = callback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifeCycleCallback != null) {
            lifeCycleCallback.glideInitEvent();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifeCycleCallback != null) {
            lifeCycleCallback.glideStopEvent();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifeCycleCallback != null) {
            lifeCycleCallback.glideRecycleEvent();
        }
    }

}
