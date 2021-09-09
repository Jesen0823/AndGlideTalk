package com.jesen.customglide.glide;

public class GlideBuilder {

    public Glide build(){
        RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();
        Glide glide = new Glide(requestManagerRetriever);
        return glide;
    }
}
