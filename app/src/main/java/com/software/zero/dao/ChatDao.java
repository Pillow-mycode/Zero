package com.software.zero.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.software.zero.pojo.ChatHistory;

@Dao
public interface ChatDao {
    @Insert
    void insertChat(ChatHistory chatHistory);
}
