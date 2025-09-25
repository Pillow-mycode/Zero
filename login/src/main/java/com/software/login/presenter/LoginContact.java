package com.software.login.presenter;

public interface LoginContact {
    interface LoginPresenter{
        void login(String username, String password);
        void dispatch();

        void register(String username, String password, String confirmPassword);
    }
    interface LoginView {

        void onUserNameEmpty();

        void onPasswordEmpty();

        void onUserNameLengthError();

        void onPasswordLengthError();

        void onSuccess(String token);

        void onFailure(String msg);

        void onError(String message);

        void onConfirmPasswordEmpty();

        void onPasswordNotMatch();

        void onRegisteSuccess(String token);

        void onRegisterFailure(String msg);
    }
}
