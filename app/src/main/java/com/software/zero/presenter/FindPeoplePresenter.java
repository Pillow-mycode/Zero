package com.software.zero.presenter;


import com.software.zero.activity.FindPeopleActivity;
import com.software.zero.contract.FindPeopleContract;
import com.software.zero.model.FindPeopleModel;
import com.software.zero.response.Response;
import com.software.zero.response.data.FindPeopleData;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class FindPeoplePresenter implements FindPeopleContract.Presenter {
    private FindPeopleContract.View view;
    private FindPeopleModel model;
    private Disposable disposable;
    public FindPeoplePresenter(FindPeopleActivity findPeopleActivity) {
        this.view = findPeopleActivity;
        model = new FindPeopleModel();
    }
    @Override
    public void searchMessage(String etText) {
        Observable<Response<FindPeopleData>> observable = model.findPeople(etText);
        disposable = observable.subscribe(r -> {
            List<FindPeopleData.SearchMessage> list = r.getData().getList();
            view.onFindSuccess(list);
        }, e -> {
            view.onError(e);
        });
    }

    @Override
    public void dispatch() {
        view = null;
        disposable.dispose();
        model = null;
    }
}
