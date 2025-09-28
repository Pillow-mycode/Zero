package com.software.zero.contract;

import com.software.zero.response.data.FindPeopleData;

import java.util.List;

public interface FindPeopleContract {
    interface Presenter {

        void searchMessage(String etText);
        void dispatch();
    }

    interface View {

        void onFindSuccess(List<FindPeopleData.SearchMessage> list);

        void onError(Throwable e);
    }
}
