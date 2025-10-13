package com.software.zero.api;


import com.software.zero.response.Response;
import com.software.zero.response.data.FindPeopleData;
import com.software.zero.response.data.FriendRequestData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FindPeopleApi {
    @POST("/zero-api/find-people")
    @FormUrlEncoded
    Observable<Response<FindPeopleData>> findPeopleList(@Field("userNameOrPhoneNumber") String text);

    @POST("/zero-api/add-people")
    @FormUrlEncoded
    Observable<Response<Boolean>> addPeople(@Field("phoneNumber")String phoneNumber);


}
