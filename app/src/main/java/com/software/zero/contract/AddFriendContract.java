package com.software.zero.contract;

import com.software.zero.response.data.FriendRequestData;

public interface AddFriendContract {
    interface View {

        void onAccept(FriendRequestData friendMessage);

        void onFail();

        void onError(Throwable e);

        void onRejectAccept(String phoneNumber);
    }
    interface Presenter {
        void acceptFriend(String phoneNumber, String flag);
        void dispatch();
    }
}
