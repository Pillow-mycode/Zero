package com.software.zero.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.software.zero.R;
import com.software.zero.adapter.FriendRequestAdapter;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.repository.AddFriendRepository;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView rv_friend_list;
    private FriendRequestAdapter friendRequestAdapter;
    private AddFriendRepository addFriendRepository = new AddFriendRepository();
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        rv_friend_list = findViewById(R.id.rv_friend_requests);
        rv_friend_list.setLayoutManager(new LinearLayoutManager(this));
        List<AddFriendMessage> allRequest = addFriendRepository.findAllRequest();
        friendRequestAdapter = new FriendRequestAdapter(allRequest);
        rv_friend_list.setAdapter(friendRequestAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addFriendRepository.updateFriend();
    }
}