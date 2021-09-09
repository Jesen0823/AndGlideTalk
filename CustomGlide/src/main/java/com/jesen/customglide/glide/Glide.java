package com.jesen.customglide.glide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.jesen.customglide.MainActivity;

public class Glide {

    RequestManagerRetriever retriever;

    public Glide(RequestManagerRetriever retriever){
        this.retriever = retriever;
    }


    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * RequestManager 由 RequestManagerRetriever 创建
     * */
    public static RequestManagerRetriever getRetriever(Context context){
        return Glide.get(context).getRetriever();
    }

    public static Glide get(Context context){
        return new GlideBuilder().build();
    }

    public RequestManagerRetriever getRetriever(){
        return retriever;
    }
}
