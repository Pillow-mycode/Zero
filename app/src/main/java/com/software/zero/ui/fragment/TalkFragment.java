package com.software.zero.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.software.util.dialog.LoadingDialog;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.MyApp;
import com.software.zero.R;
import com.software.zero.adapter.ChatAdapter;
import com.software.zero.contract.ChatContract;
import com.software.zero.enums.UserProperty;
import com.software.zero.pojo.ChatHistory;
import com.software.zero.pojo.PeopleMessage;
import com.software.zero.pojo.WebSocketMessageEvent;
import com.software.zero.presenter.ChatPresenter;
import com.software.zero.repository.ChatRepository;
import com.software.zero.repository.MessageRepository;
import com.software.zero.response.data.FriendRequestData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TalkFragment extends Fragment implements ChatContract.View {
    private EditText editText;
    private ImageButton sendButton;
    private RecyclerView recyclerView;
    private ChatRepository repository;
    private ChatAdapter chatAdapter; // 声明为成员变量，方便后续更新
    private TextView tvNote;

    private ChatPresenter chatPresenter;
    private LoadingDialog dialog;
    private PeopleMessage me, other;

    private MessageRepository messageRepository;


    public TalkFragment(){}

    @Override
    public void onResume() {
        super.onResume();
        dialog.show();

        // 在后台线程执行数据库操作
        new Thread(() -> {
            try {
                List<ChatHistory> list = repository.selectMessage();
                me = MyApp.getMyMessage();
                other = MyApp.getTheOtherMessage();
                if(getActivity()!=null) getActivity().runOnUiThread(() -> {
                    tvNote.setText(other.getUser_name());
                    chatAdapter = new ChatAdapter(me, other);
                    recyclerView.setAdapter(chatAdapter);
                    chatAdapter.loadMessage(list);
                    scrollToBottom();
                    dialog.dismiss();
                });
            } catch (Exception e) {
                android.util.Log.e("TalkFragment", "加载聊天记录失败", e);
            }
        }).start();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_talk_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        dialog = new LoadingDialog(getContext());
        editText = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.sendButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvNote = view.findViewById(R.id.tvNote);
        repository = new ChatRepository();
        chatPresenter = new ChatPresenter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRepository = new MessageRepository();

        sendButton.setOnClickListener(v -> {
            String message = editText.getText().toString();
            editText.setText("");
            ChatHistory chatHistory = new ChatHistory(message, true);
            // 添加新消息到 adapter 并刷新
            chatAdapter.addMessage(chatHistory);
            scrollToBottom();
            
            // 在后台线程执行数据库操作
            new Thread(() -> {
                try {
                    repository.insertChat(chatHistory);
                } catch (Exception e) {
                    android.util.Log.e("TalkFragment", "插入聊天记录失败", e);
                }
            }).start();
            
            chatPresenter.sendMessage(message);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(WebSocketMessageEvent event) {
        String type = event.getMessageType();
        if(type.equals("chat_message") && isResumed()) {
            String message = event.getPayloadJson();
            // 创建新的聊天记录并添加到 adapter
            ChatHistory chatHistory = new ChatHistory(message, false);
            chatAdapter.addMessage(chatHistory);
            scrollToBottom();
        }
    }

    // 滚动到底部的方法
    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    public static TalkFragment newInstance() {
        return new TalkFragment();
    }

    @Override
    public void onSendError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(getActivity(), "发送失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatPresenter.dispatch();
        EventBus.getDefault().unregister(this);
    }
}