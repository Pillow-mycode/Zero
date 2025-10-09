package com.software.zero.repository;

import com.software.zero.dao.ChatDao;
import com.software.zero.database.ZeroDatabase;
import com.software.zero.pojo.ChatHistory;

public class ChatRepository {
    private ZeroDatabase zeroDatabase;
    private final ChatDao dao;

    public ChatRepository(){
        zeroDatabase = ZeroDatabase.getDatabase();
        dao = zeroDatabase.chatDao();
    }

    public void insertChat(ChatHistory chatHistory){
        dao.insertChat(chatHistory);
    }
}
