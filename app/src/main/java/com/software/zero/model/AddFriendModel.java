package com.software.zero.model;

import com.software.util.retrofit.MyRetrofit;
import com.software.zero.api.AddFriendApi;
import com.software.zero.response.Response;
import com.software.zero.response.data.FriendRequestData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddFriendModel {
    private final MyRetrofit retrofit = MyRetrofit.getInstance();
    private final AddFriendApi api = retrofit.create(AddFriendApi.class);;
    public Observable<Response<FriendRequestData>> acceptFriend(String phoneNumber, String flag) {
        return api.acceptFriend(phoneNumber,flag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<FriendRequestData>> findFriend() {
        return api.findFriend()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
