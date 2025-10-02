package com.software.zero.ui.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.software.util.dialog.LoadingDialog;
import com.software.zero.R;
import com.software.zero.adapter.FindPeopleAdapter;
import com.software.zero.contract.FindPeopleContract;
import com.software.zero.presenter.FindPeoplePresenter;
import com.software.zero.response.data.FindPeopleData;

import java.util.List;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleContract.View {

    private EditText et_find_people;
    private Button bt_search;
    private RecyclerView rv_people;
    private FindPeopleContract.Presenter presenter;
    private FindPeopleAdapter findPeopleAdapter;
    private LoadingDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);
        init();
        bt_search.setOnClickListener(v -> {
            loading.show();
            String et_text = et_find_people.getText().toString(); // 拿到搜索关键字
            presenter.searchMessage(et_text); // 搜索信息
        });
    }

    private void init() { // 初始化控件，包括recyclerview及其Adapter
        loading = new LoadingDialog(this);
        et_find_people = findViewById(R.id.et_search);
        bt_search = findViewById(R.id.btn_search);
        rv_people = findViewById(R.id.rv_search_results);
        presenter = new FindPeoplePresenter(this);
        rv_people.setLayoutManager(new LinearLayoutManager(this));
        findPeopleAdapter = new FindPeopleAdapter();
        findPeopleAdapter.setOnAddUserListener((phoneNumber, holder) ->  {
            loading.show();
            presenter.addUser(phoneNumber, holder);
        });
        rv_people.setAdapter(findPeopleAdapter);
    }

    @Override
    public void onFindSuccess(List<FindPeopleData.SearchMessage> list) {
        loading.dismiss();
        findPeopleAdapter.updateAdapter(list); // 更新adapter
        Toast.makeText(this, "搜索成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        loading.dismiss();
        Toast.makeText(this, "网络请求错误，请检查网络连接", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddSuccess(FindPeopleAdapter.ViewHolder viewHolder) {
        loading.dismiss();
        viewHolder.button.setVisibility(GONE);
        viewHolder.requested.setVisibility(VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dispatch();
    }
}
