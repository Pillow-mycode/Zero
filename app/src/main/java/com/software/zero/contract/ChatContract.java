package com.software.zero.contract;

public interface ChatContract {
    interface Presenter {
        void sendMessage(String message);


        void dispatch();
    }
    interface View {

        void onSendError(Throwable e);
    }
}
