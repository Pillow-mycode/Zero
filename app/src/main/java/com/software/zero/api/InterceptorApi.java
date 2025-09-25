package com.software.zero.api;

import com.software.zero.response.Response;
import com.software.zero.response.data.InterceptorData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InterceptorApi {

    @POST("/token-check")
    @FormUrlEncoded
    Observable<Response<InterceptorData>> checkTokenEffect(@Field("token") String authToken);
}
