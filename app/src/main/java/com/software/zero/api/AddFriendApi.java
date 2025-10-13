package com.software.zero.api;

import com.software.zero.MyApp;
import com.software.zero.response.Response;
import com.software.zero.response.data.FriendRequestData;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AddFriendApi {
    @POST("/zero-api/accept-friend")
    @FormUrlEncoded
    Observable<Response<FriendRequestData>> acceptFriend(@Field("phoneNumber") String phoneNumber, @Field("flag") String flag);

    @GET("/zero-api/find-friend")
    Observable<Response<FriendRequestData>> findFriend();

    @POST("/zero-api/find-peoples")
    @FormUrlEncoded
    Observable<Response<FriendRequestData>> findPeople(@Field("phoneNumber") String phoneNumber);
}