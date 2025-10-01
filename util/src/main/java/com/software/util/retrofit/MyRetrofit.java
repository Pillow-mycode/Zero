package com.software.util.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyRetrofit {

    private static volatile MyRetrofit instance;
    private Retrofit retrofit;
    public static void init(Builder builder) {
        if(instance == null) {
            synchronized (MyRetrofit.class) {
                if(instance == null) {
                    instance = builder.build();
                }
            }
        }
    }


    private MyRetrofit(String BASE_URL, List<Interceptor> interceptors) {
        // 创建 Gson 实例，可配置各种解析规则
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期格式
                .serializeNulls() // 序列化null值
                .create();

        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 设置日志级别


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        OkHttpClient client = new OkHttpClient(builder);

        // 创建 Retrofit 实例
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)) // 添加Gson转换器
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 添加RxJava适配器
                .build();
    }

    public static MyRetrofit getInstance() {
        return instance;
    }
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static class Builder {
        private String baseUrl;
        private List<Interceptor> interceptors = new ArrayList<>();

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public MyRetrofit build() {
            return new MyRetrofit(baseUrl, interceptors);
        }
    }
}