package com.software.login.model;


import com.software.login.response.LoginResponse;
import com.software.login.response.Response;

import io.reactivex.rxjava3.core.Observable;

public interface LoginModel {
    Observable<Response<LoginResponse>> login(String username, String password);

    Observable<Response<LoginResponse>> register(String username, String password, String confirmPassword);
}
