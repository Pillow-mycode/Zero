package com.software.util.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class MyRetrofit {

    private static volatile MyRetrofit instance;
    private Retrofit retrofit;

    private static String BASE_URL;

    private MyRetrofit(String BASE_URL) {
        MyRetrofit.BASE_URL = BASE_URL;
        // 私有构造函数，防止外部实例化
        initRetrofit();
    }

    public static MyRetrofit getInstance(String BASE_URL) {
        if (instance == null) {
            synchronized (MyRetrofit.class) {
                if (instance == null) {
                    instance = new MyRetrofit(BASE_URL);
                }
            }
        }
        return instance;
    }

    private void initRetrofit() {
        // 创建 Gson 实例，可配置各种解析规则
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期格式
                .serializeNulls() // 序列化null值
                .create();

        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 设置日志级别

        // 创建 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor) // 添加日志拦截器
                // 可以添加其他拦截器，如添加统一请求头等
                // .addInterceptor(new Interceptor() {
                //     @Override
                //     public Response intercept(Chain chain) throws IOException {
                //         Request original = chain.request();
                //         Request request = original.newBuilder()
                //                 .header("Authorization", "Bearer your_token")
                //                 .method(original.method(), original.body())
                //                 .build();
                //         return chain.proceed(request);
                //     }
                // })
                .build();

        // 创建 Retrofit 实例
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)) // 添加Gson转换器
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 添加RxJava适配器
                .build();
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}