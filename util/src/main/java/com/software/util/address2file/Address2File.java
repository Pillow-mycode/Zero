package com.software.util.address2file;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.software.util.R;

import java.io.File;
import java.net.URI;

public class Address2File {
    private String baseUrl;
    private String url;
    private String network;
    private static volatile Address2File instance;

    private Address2File(String baseUrl, String url, String network) {
        this.baseUrl =  baseUrl;
        this.url = url;
        this.network = network;
    }

    private Address2File(Builder builder) {
        baseUrl = builder.baseUrl;
        url = builder.url;
        network = builder.network;
    }

    public static void init(Builder builder) {
        instance = new Address2File(builder);
    }

    public static Address2File getInstance() {
        if(instance == null) throw new RuntimeException("Address2File应该被初始化");
        return instance;
    }


    /**
     * 预加载并缓存图片，但不显示
     * @param context 上下文
     * @param fileName 文件名
     */
    public static void preloadImage(Context context, String fileName) {
        String imageUrl = getInstance().network + "://" + getInstance().baseUrl + fileName;
        Glide.with(context)
                .load(imageUrl)
                .preload();  // 预加载图片到缓存
    }

    // 如果需要同时支持显示图片的功能
    public static void invoke(Context context, String fileName, ImageView imgView) {
        String imageUrl = getInstance().network + "://" + getInstance().baseUrl + getInstance().url +'/'+ fileName;
        Glide.with(context)
                .load(imageUrl)
                .into(imgView);
    }

    public static class Builder{
        private String baseUrl;
        private String url;
        private String network = "http";

        public Builder(String baseUrl, String url, String network) {
            this.baseUrl = baseUrl;
            this.url = url;
            this.network = network;
        }

        public Builder() {
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setNetwork(String network) {
            this.network = network;
            return this;
        }

        public Address2File build() {
            return new Address2File(new Builder(baseUrl, url, network));
        }
    }
}
