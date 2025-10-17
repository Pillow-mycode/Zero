package com.software.zero.presenter;


import android.util.Log;

import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.zero.adapter.FindPeopleAdapter;
import com.software.zero.ui.activity.FindPeopleActivity;
import com.software.zero.contract.FindPeopleContract;
import com.software.zero.model.FindPeopleModel;
import com.software.zero.response.Response;
import com.software.zero.response.data.FindPeopleData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class FindPeoplePresenter implements FindPeopleContract.Presenter {
    private FindPeopleContract.View view;
    private FindPeopleModel model;
    private List<Disposable> disposable = new ArrayList<>();
    private EncryptedPrefsHelper sharePrefence;
    public FindPeoplePresenter(FindPeopleActivity findPeopleActivity) {
        this.view = findPeopleActivity;
        model = new FindPeopleModel();
        sharePrefence = EncryptedPrefsHelper.getInstance();
    }
    @Override
    public void searchMessage(String etText) {
        Observable<Response<FindPeopleData>> observable = model.findPeople(etText);
        Disposable subscribe = observable.subscribe(r -> {
            List<FindPeopleData.SearchMessage> list = r.getData().getList();
            view.onFindSuccess(list);
        }, e -> {
            view.onError(e);
        });
        disposable.add(subscribe);
    }

    @Override
    public void dispatch() {
        view = null;
        model = null;
        for (Disposable disposable1 : disposable) {
            disposable1.dispose();
        }
    }

    @Override
    public void addUser(String phoneNumber, FindPeopleAdapter.ViewHolder viewHolder) {
        Observable<Response<Boolean>> observable = model.addUser(phoneNumber);
        Disposable subscribe = observable.subscribe(r -> {
            if(r.isSuccess()) {
                sharePrefence.saveBoolean(phoneNumber, true);
                view.onAddSuccess(viewHolder);
            } else {
                view.onAddFail();
            }
        }, e-> {
            e.printStackTrace();
            view.onAddFail();
        });

        disposable.add(subscribe);
    }
}
