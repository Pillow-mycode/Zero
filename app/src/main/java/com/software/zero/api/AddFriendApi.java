package com.software.zero.api;


import com.software.zero.response.Response;
import com.software.zero.response.data.FriendRequestData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AddFriendApi {
    @POST("/accept-friend")
    @FormUrlEncoded
    Observable<Response<FriendRequestData>> acceptFriend(@Field("phoneNumber") String phoneNumber, @Field("flag") String flag);

    @GET("/find-friend")
    Observable<Response<FriendRequestData>> findFriend();
}
