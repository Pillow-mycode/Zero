package com.software.zero.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.software.zero.pojo.ChatHistory;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    void insertChat(ChatHistory chatHistory);

    @Query("SELECT * FROM chat_history")
    List<ChatHistory> selectMessage();
}