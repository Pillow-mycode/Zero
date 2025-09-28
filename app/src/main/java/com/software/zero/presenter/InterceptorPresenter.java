package com.software.zero.presenter;

import android.util.Log;

import com.software.zero.contract.InterceptorContract;
import com.software.zero.model.InterceptorModel;
import com.software.zero.response.Response;
import com.software.zero.response.data.InterceptorData;
import com.software.zero.activity.InterceptorActivity;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class InterceptorPresenter implements InterceptorContract.Presenter {

    private static final String TAG = "Interceptor";
    private InterceptorContract.View view;
    private InterceptorModel model;
    Disposable disposable;
    public InterceptorPresenter(InterceptorActivity interceptorActivity) {
        view = interceptorActivity;
        model = new InterceptorModel();
    }

    @Override
    public void checkTheTokenEffect(String authToken) {
        Observable<Response<InterceptorData>> observable = model.checkTokenEffect(authToken);
        disposable = observable.subscribe(r -> {
            Log.d(TAG, "checkTheTokenEffect: " + r);
            boolean isAccept = r.isSuccess();
            if(isAccept) {
                view.onTokenAccept();
                return;
            } else {
                view.onTokenWrong();
                return;
            }
        }, e -> {
            view.onVisitServerError();
            throw e;
        });
    }

    @Override
    public void dispatch() {
        view = null;
        disposable.dispose();
    }
}
