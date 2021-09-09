package com.jesen.customglide.loaddata;

import android.content.Context;

/**
 * 加载外部资源
 */

public interface ILoadData {

     void loadResource(String path, LoadResponseListener responseListener, Context context);
}
