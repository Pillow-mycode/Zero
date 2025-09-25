package com.software.zero.contract;

public interface InterceptorContract {
    interface Presenter {

        void checkTheTokenEffect(String authToken);

        void dispatch();
    }

    interface View {

        void onTokenAccept();

        void onTokenWrong();

        void onVisitServerError();
    }
}
