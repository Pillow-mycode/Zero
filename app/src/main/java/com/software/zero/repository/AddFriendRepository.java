package com.software.zero.repository;

import com.software.zero.dao.AddFriendMessageDao;
import com.software.zero.database.ZeroDatabase;
import com.software.zero.pojo.AddFriendMessage;

import java.util.List;

public class AddFriendRepository {
    private ZeroDatabase zeroDatabase;
    private AddFriendMessageDao dao;

    public AddFriendRepository(){
        zeroDatabase = ZeroDatabase.getDatabase();
        dao = zeroDatabase.addFriendMessageDao();
    }

    public void insertMessage(AddFriendMessage addFriendMessage) {
        dao.insertMessage(addFriendMessage);
    }

    public boolean checkNewMessage() {
        AddFriendMessage addFriendMessage = dao.findNewMessage();
        return addFriendMessage != null;
    }

    public List<AddFriendMessage> findAllRequest() {
        return dao.findAllRequest();
    }

    public void updateFriend() {
        dao.updateFriend();
    }
    public void deleteRequest(String phoneNumber) {
        dao.deleteRequest(phoneNumber);
    }
}
