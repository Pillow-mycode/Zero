package com.software.zero.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_history")
public class ChatHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String message_content;
    private Boolean mine;


    public ChatHistory(String message_content, Boolean mine) {
        this.message_content = message_content;
        this.mine = mine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public Boolean getMine() {
        return mine;
    }

    public void setMine(Boolean mine) {
        this.mine = mine;
    }
}
