package com.software.zero.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.software.zero.R;
import com.software.zero.contract.ChatContract;
import com.software.zero.pojo.ChatHistory;
import com.software.zero.presenter.ChatPresenter;
import com.software.zero.repository.ChatRepository;

public class TalkFragment extends Fragment implements ChatContract.View {
    private EditText editText;
    private ImageButton sendButton;
    private RecyclerView recyclerView;
    private ChatRepository repository;

    private ChatPresenter chatPresenter;


    public TalkFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_talk_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editText = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.sendButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        repository = new ChatRepository();
        chatPresenter = new ChatPresenter(this);


        sendButton.setOnClickListener(v -> {
            String message = editText.getText().toString();
            editText.setText("");
            ChatHistory chatHistory = new ChatHistory(message, true);
            repository.insertChat(chatHistory);
            chatPresenter.sendMessage(message);
        });
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
    }
}
