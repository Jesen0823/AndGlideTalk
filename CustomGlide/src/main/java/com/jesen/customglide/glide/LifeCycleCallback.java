package com.jesen.customglide.glide;

public interface LifeCycleCallback {

    void  glideInitEvent();

    void glideStopEvent();

    void glideRecycleEvent();
}
