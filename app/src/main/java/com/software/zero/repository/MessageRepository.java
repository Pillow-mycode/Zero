package com.software.zero.repository;

import android.content.Context;

import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.MyApp;
import com.software.zero.dao.MessageDao;
import com.software.zero.database.ZeroDatabase;
import com.software.zero.manager.DatabaseManager;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.pojo.PeopleMessage;

import java.util.List;

public class MessageRepository {
    private ZeroDatabase zeroDatabase;
    private MessageDao dao;

    public MessageRepository(){
        Context context = MyApp.getInstance();
        String nowUser = TokenPrefsHelper.getInstance().getString("now-user");
        zeroDatabase = DatabaseManager.getDatabase(context, nowUser);
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
        new Thread(() -> {
            dao.deleteRequest(phoneNumber);
        }).start();
    }

    public void updatePeople(PeopleMessage message) {
        new Thread(() -> {
            String allPeople = dao.findAllPeople(message.getPhone_number());
            if(allPeople == null || allPeople.isEmpty()) dao.insertPeople(message.getProfile_picture(), message.getPhone_number(), message.getUser_name());
            dao.updatePeople(message.getProfile_picture(), message.getPhone_number(), message.getUser_name());
        }).start();
    }

    public PeopleMessage findPeopleMessage(String phoneNumber) {
        return dao.findPeopleMessage(phoneNumber).get(0);
    }
}
