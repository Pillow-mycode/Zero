package com.software.zero.presenter;

import android.util.Log;

import com.software.zero.contract.ChatContract;
import com.software.zero.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class ChatPresenter implements ChatContract.Presenter {
    private static final String TAG = "ChatPresenter";
    private List<Disposable> disposables = new ArrayList<>();

    private ChatModel chatModel;
    private ChatContract.View view;

    public ChatPresenter(ChatContract.View view) {
        this.view = view;
        chatModel = new ChatModel();
    }

    @Override
    public void sendMessage(String message) {
        Disposable subscribe = chatModel.sendMessage(message)
                .subscribe(r -> {
                    Log.d(TAG, "sendMessage: " + "发送成功");
                }, e -> {
                    view.onSendError(e);
                });

        disposables.add(subscribe);
    }


    @Override
    public void dispatch() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        view = null;
    }
}
