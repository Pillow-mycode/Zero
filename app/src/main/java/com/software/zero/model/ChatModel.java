package com.software.zero.model;

import com.software.util.retrofit.MyRetrofit;
import com.software.zero.api.ChatApi;
import com.software.zero.response.Response;
import com.software.zero.response.data.ChatData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatModel {
    private final MyRetrofit retrofit = MyRetrofit.getInstance();
    private ChatApi api = retrofit.create(ChatApi.class);
    public Observable<Response<ChatData>> sendMessage(String message) {
        return api.sendMessage(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
