package com.software.zero.api;

import com.software.zero.response.Response;
import com.software.zero.response.data.InterceptorData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SendLocation {

    @POST("/zero-api/location")
    @FormUrlEncoded
    Observable<Response<InterceptorData>> sendLocation(@Field("latitude") double latitude , @Field("longitude") double longitude);
}
