package com.software.zero.presenter;

import com.software.zero.contract.AddFriendContract;
import com.software.zero.model.AddFriendModel;
import com.software.zero.response.data.FriendRequestData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private AddFriendModel model;
    private AddFriendContract.View view;

    private List<Disposable> subscribe = new ArrayList<>();
    public AddFriendPresenter(AddFriendContract.View view) {
        this.view = view;
        model = new AddFriendModel();
    }

    @Override
    public void acceptFriend(String phoneNumber, String flag) {
        subscribe.add(model.acceptFriend(phoneNumber, flag)
                .subscribe(r -> {
                    FriendRequestData friendMessage = r.getData();
                    if(!r.isSuccess()) {
                        if (flag.equals("accept")) view.onFail();
                        else view.onRejectAccept(phoneNumber);
                    }
                    else {
                        if(flag.equals("accept")) view.onAccept(friendMessage);
                        else view.onRejectAccept(phoneNumber);
                    }

                }, e -> {
                    view.onError(e);
                }));
    }

    @Override
    public void dispatch() {
        view = null;
        for (Disposable disposable : subscribe) {
            disposable.dispose();
        }
    }
}
