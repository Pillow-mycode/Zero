package com.software.zero.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.software.util.dialog.LoadingDialog;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.zero.R;
import com.software.zero.adapter.FriendRequestAdapter;
import com.software.zero.contract.AddFriendContract;
import com.software.zero.enums.UserProperty;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.presenter.AddFriendPresenter;
import com.software.zero.repository.AddFriendRepository;
import com.software.zero.response.data.FriendRequestData;

import java.util.List;

public class FriendRequestActivity extends AppCompatActivity implements AddFriendContract.View {
    private RecyclerView rv_friend_list;
    private FriendRequestAdapter friendRequestAdapter;
    private AddFriendRepository addFriendRepository = new AddFriendRepository();
    private LoadingDialog dialog;
    private AddFriendPresenter presenter;
    private EncryptedPrefsHelper encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();
    private int nowPosition;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        dialog = new LoadingDialog(this);
        presenter = new AddFriendPresenter(this);
        rv_friend_list = findViewById(R.id.rv_friend_requests);
        rv_friend_list.setLayoutManager(new LinearLayoutManager(this));
        List<AddFriendMessage> allRequest = addFriendRepository.findAllRequest();
        friendRequestAdapter = new FriendRequestAdapter(allRequest);
        friendRequestAdapter.setListener(new FriendRequestAdapter.AdapterListener() {
            @Override
            public void acceptFriend(String phoneNumber, int position) {
                nowPosition = position;
                dialog.show();
                presenter.acceptFriend(phoneNumber, "accept");
            }

            @Override
            public void rejectFriend(String phoneNumber, int position) {
                nowPosition = position;
                dialog.show();
                presenter.acceptFriend(phoneNumber, "reject");
            }
        });
        rv_friend_list.setAdapter(friendRequestAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addFriendRepository.updateFriend();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        friendRequestAdapter = null;
    }

    @Override
    public void onAccept(FriendRequestData friendMessage) {
        dialog.dismiss();
        encryptedPrefsHelper.saveString(UserProperty.PROFILE_PICTURE.getPropertyName(), friendMessage.getProfile_picture());
        encryptedPrefsHelper.saveString(UserProperty.USERNAME.getPropertyName(), friendMessage.getUser_name());
        encryptedPrefsHelper.saveString(UserProperty.PHONE_NUMBER.getPropertyName(), friendMessage.getPhone_number());


        List<AddFriendMessage> friendRequestList = friendRequestAdapter.getFriendRequestList();
        addFriendRepository.deleteRequest(friendRequestList.get(nowPosition).getPhone_number());

        // 完善更新表项操作：移除列表项并通知适配器
        friendRequestList.remove(nowPosition);
        friendRequestAdapter.notifyItemRemoved(nowPosition);
        friendRequestAdapter.notifyItemRangeChanged(nowPosition, friendRequestList.size());



        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onFail() {
        dialog.dismiss();
        List<AddFriendMessage> friendRequestList = friendRequestAdapter.getFriendRequestList();
        addFriendRepository.deleteRequest(friendRequestList.get(nowPosition).getPhone_number());

        // 完善更新表项操作：移除列表项并通知适配器
        friendRequestList.remove(nowPosition);
        friendRequestAdapter.notifyItemRemoved(nowPosition);
        friendRequestAdapter.notifyItemRangeChanged(nowPosition, friendRequestList.size());



        Toast.makeText(this, "对方已有好友", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        dialog.dismiss();
        e.printStackTrace();
        Toast.makeText(this, "网络请求错误，请检查网络连接", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectAccept(String phoneNumber) {
        List<AddFriendMessage> friendRequestList = friendRequestAdapter.getFriendRequestList();
        addFriendRepository.deleteRequest(friendRequestList.get(nowPosition).getPhone_number());

        // 完善更新表项操作：移除列表项并通知适配器
        friendRequestList.remove(nowPosition);
        friendRequestAdapter.notifyItemRemoved(nowPosition);
        friendRequestAdapter.notifyItemRangeChanged(nowPosition, friendRequestList.size());


        dialog.dismiss();
        Toast.makeText(this, "已拒绝", Toast.LENGTH_SHORT).show();
    }
}