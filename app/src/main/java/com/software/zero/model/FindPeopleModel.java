package com.software.zero.model;


import com.software.util.retrofit.MyRetrofit;
import com.software.zero.api.FindPeopleApi;
import com.software.zero.config.ServicerConfig;
import com.software.zero.contract.FindPeopleContract;
import com.software.zero.response.Response;
import com.software.zero.response.data.FindPeopleData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FindPeopleModel implements FindPeopleContract {
    private final MyRetrofit retrofit = MyRetrofit.getInstance(ServicerConfig.getURL());
    public Observable<Response<FindPeopleData>> findPeople(String etText) {
        FindPeopleApi api = retrofit.create(FindPeopleApi.class);
        return api.findPeopleList(etText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
