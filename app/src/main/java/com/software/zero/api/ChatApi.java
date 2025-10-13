package com.software.zero.api;

import com.software.zero.response.Response;
import com.software.zero.response.data.ChatData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatApi {
    @POST("/zero-api/send-message")
    @FormUrlEncoded
    Observable<Response<ChatData>> sendMessage(@Field("message") String message);
}
