package com.software.login.model.impl;


import com.software.login.api.LoginApi;
import com.software.login.config.ServicerBaseUrl;
import com.software.login.model.LoginModel;
import com.software.login.response.LoginResponse;
import com.software.login.response.Response;
import com.software.util.retrofit.MyRetrofit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginModelImpl implements LoginModel {
    private final MyRetrofit myRetrofit = MyRetrofit.getInstance(ServicerBaseUrl.getURL());

    @Override
    public Observable<Response<LoginResponse>> login(String username, String password) {
        LoginApi loginApi = myRetrofit.create(LoginApi.class);
        Observable<Response<LoginResponse>> login = loginApi.login(username, password);
        return login.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<LoginResponse>> register(String username, String password, String confirmPassword) {
        LoginApi loginApi = myRetrofit.create(LoginApi.class);
        Observable<Response<LoginResponse>> register = loginApi.register(username, password, confirmPassword);
        return register.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
