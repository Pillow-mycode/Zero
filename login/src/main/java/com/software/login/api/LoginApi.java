package com.software.login.api;


import com.software.login.response.LoginResponse;
import com.software.login.response.Response;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("/zero-api/login")
    @FormUrlEncoded
    Observable<Response<LoginResponse>> login(@Field("phoneNumber") String username,@Field("password") String password);

    @POST("/zero-api/register")
    @FormUrlEncoded
    Observable<Response<LoginResponse>> register(@Field("phoneNumber") String username,@Field("password") String password,
                                                 @Field("confirmPassword") String confirmPassword);

}
