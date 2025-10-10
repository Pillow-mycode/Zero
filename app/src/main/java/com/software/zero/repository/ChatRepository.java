package com.software.zero.repository;

import android.content.Context;

import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.MyApp;
import com.software.zero.dao.ChatDao;
import com.software.zero.database.ZeroDatabase;
import com.software.zero.manager.DatabaseManager;
import com.software.zero.pojo.ChatHistory;

import java.util.List;

public class ChatRepository {
    private ZeroDatabase zeroDatabase;
    private final ChatDao dao;

    public ChatRepository(){
        Context context = MyApp.getInstance();
        String nowUser = TokenPrefsHelper.getInstance().getString("now-user");
        zeroDatabase = DatabaseManager.getDatabase(context, nowUser);
        dao = zeroDatabase.chatDao();
    }

    public void insertChat(ChatHistory chatHistory){
        dao.insertChat(chatHistory);
    }

    public List<ChatHistory> selectMessage() {
        return dao.selectMessage();
    }
}