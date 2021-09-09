package com.jesen.customglide;

import android.app.Application;

public class BaseApplication extends Application {

    private static Application INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }

    public static Application getApplication(){
        if (INSTANCE != null){
            return INSTANCE;
        }
        return null;
    }
}
