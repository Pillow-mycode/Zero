package com.software.zero;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.config.ServicerConfig;
import com.software.util.address2file.Address2File;
import com.software.util.retrofit.MyRetrofit;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.pojo.PeopleMessage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class MyApp extends Application {
    private static MyApp instance;
    public static String url = ServicerConfig.getServicerAddress() + ":8080";
    private static TokenPrefsHelper tokenPrefsHelper;
    private static PeopleMessage myMessage, theOtherMessage;

    public static PeopleMessage getMyMessage() {
        return myMessage;
    }

    public static void setMyMessage(PeopleMessage myMessage) {
        MyApp.myMessage = myMessage;
    }

    public static PeopleMessage getTheOtherMessage() {
        return theOtherMessage;
    }

    public static void setTheOtherMessage(PeopleMessage theOtherMessage) {
        MyApp.theOtherMessage = theOtherMessage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TokenPrefsHelper.init(this);
        tokenPrefsHelper = TokenPrefsHelper.getInstance();
        MyRetrofit.init(new MyRetrofit.Builder()
                .setBaseUrl("http://" + url)
                .addInterceptor(new TokenInterceptor())
        );

        Address2File.init(new Address2File.Builder()
                .setNetwork("http")
                .setBaseUrl(url)
                .setUrl("/uploads")
        );
    }

    public static MyApp getInstance() {
        return instance;
    }

    private static class TokenInterceptor implements Interceptor {
        private static final String TAG = "MyApp";
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            // 获取原始请求
            okhttp3.Request originalRequest = chain.request();


            // 从加密存储中获取token
            String token = tokenPrefsHelper.getAuthToken();

            // 如果token存在，添加到请求头中
            if (token != null && !token.isEmpty()) {
                okhttp3.Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(originalRequest.method(), originalRequest.body());

                okhttp3.Request newRequest = requestBuilder.build();
                return chain.proceed(newRequest);
            }

            // 如果没有token，直接执行原始请求
            return chain.proceed(originalRequest);
        }
    }

}
