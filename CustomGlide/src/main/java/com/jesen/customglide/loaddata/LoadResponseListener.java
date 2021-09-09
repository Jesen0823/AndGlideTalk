package com.jesen.customglide.loaddata;

import com.jesen.customglide.resource.Value;

/**
 * 加载外部资源回调
 * */
public interface LoadResponseListener {

    void responseSuccess(Value value);
    void responseException(Exception e);
}
