package com.software.zero.contract;

import com.software.zero.adapter.FindPeopleAdapter;
import com.software.zero.response.data.FindPeopleData;

import java.util.List;

public interface FindPeopleContract {
    interface Presenter {

        void searchMessage(String etText);
        void dispatch();

        void addUser(String phoneNumber, FindPeopleAdapter.ViewHolder viewHolder);
    }

    interface View {

        void onFindSuccess(List<FindPeopleData.SearchMessage> list);

        void onError(Throwable e);

        void onAddSuccess(FindPeopleAdapter.ViewHolder viewHolder);
    }
}
