package com.software.zero.model;

import com.software.util.retrofit.MyRetrofit;
import com.software.zero.api.InterceptorApi;
import com.software.zero.config.ServicerConfig;
import com.software.zero.response.Response;
import com.software.zero.response.data.InterceptorData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InterceptorModel {

    public Observable<Response<InterceptorData>> checkTokenEffect(String authToken) {
        MyRetrofit retrofit = MyRetrofit.getInstance(ServicerConfig.getURL());
        InterceptorApi api = retrofit.create(InterceptorApi.class);
        return api.checkTokenEffect(authToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
