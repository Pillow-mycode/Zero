package com.software.login.presenter.impl;

import android.util.Log;

import com.software.login.model.LoginModel;
import com.software.login.model.impl.LoginModelImpl;
import com.software.login.presenter.LoginContact;
import com.software.login.response.LoginResponse;
import com.software.login.response.Response;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginPresenterImpl implements LoginContact.LoginPresenter {
    private static final String TAG = "Login";
    private LoginContact.LoginView view;
    private LoginModel loginModel;
    private Disposable disposable;

    public LoginPresenterImpl(LoginContact.LoginView view) {
        this.view = view;
        loginModel = new LoginModelImpl();
    }

    @Override
    public void login(String username, String password) {
        if(username.isEmpty()) {
            view.onUserNameEmpty();
            return;
        }
        if(password.isEmpty()){
            view.onPasswordEmpty();
            return;
        }
        if(username.length() > 15 || username.length() < 5) {
            view.onUserNameLengthError();
            return;
        }
        if(password.length() > 15 || password.length() < 5) {
            view.onPasswordLengthError();
            return;
        }

        Observable<Response<LoginResponse>> login = loginModel.login(username, password);
        disposable = login.subscribe(r -> {
            if(r.isSuccess()) {
                Log.d(TAG, "login: " + r);
                String token = r.getData().getToken();
                view.onSuccess(token);
                dispatch();
            } else {
                String msg = r.getMessage();
                view.onFailure(msg);
            }
        }, e -> {
            view.onError(e.getMessage());
        });
    }

    @Override
    public void dispatch() {
        view = null;
        disposable.dispose();
    }

    @Override
    public void register(String username, String password, String confirmPassword) {
        if(username.isEmpty()) {
            view.onUserNameEmpty();
            return;
        }
        if(password.isEmpty()){
            view.onPasswordEmpty();
            return;
        }
        if(confirmPassword.isEmpty()) {
            view.onConfirmPasswordEmpty();
        }
        if(username.length() > 15 || username.length() < 5) {
            view.onUserNameLengthError();
            return;
        }
        if(password.length() > 15 || password.length() < 5) {
            view.onPasswordLengthError();
            return;
        }
        if(!password.equals(confirmPassword)) {
            view.onPasswordNotMatch();
            return;
        }

        Observable<Response<LoginResponse>> login = loginModel.register(username, password, confirmPassword
        );
        disposable = login.subscribe(r -> {
            if(r.isSuccess()) {
                String token = r.getData().getToken();
                view.onRegisteSuccess(token);
            } else {
                String msg = r.getMessage();
                view.onRegisterFailure(msg);
            }
        }, e -> {
            view.onError(e.getMessage());
        });
    }
}
