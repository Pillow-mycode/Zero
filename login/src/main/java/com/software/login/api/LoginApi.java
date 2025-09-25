package com.software.login.api;


import com.software.login.response.LoginResponse;
import com.software.login.response.Response;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("/login")
    @FormUrlEncoded
    Observable<Response<LoginResponse>> login(@Field("username") String username,@Field("password") String password);

    @POST("/register")
    @FormUrlEncoded
    Observable<Response<LoginResponse>> register(@Field("username") String username,@Field("password") String password,
                                                 @Field("confirmPassword") String confirmPassword);

}
