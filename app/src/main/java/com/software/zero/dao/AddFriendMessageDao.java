package com.software.zero.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.software.zero.pojo.AddFriendMessage;

import java.util.List;

@Dao
public interface AddFriendMessageDao {
    @Insert
    void insertMessage(AddFriendMessage addFriendMessage);

    @Query("select * from add_friend_message where isNew = 1")
    AddFriendMessage findNewMessage();

    @Query("select * from add_friend_message where hasRefuse = 0")
    List<AddFriendMessage> findAllRequest();

    @Query("UPDATE add_friend_message SET isNew = 0 WHERE isNew = 1")
    void updateFriend();

    @Query("DELETE FROM add_friend_message WHERE phone_number = :phoneNumber")
    void deleteRequest(String phoneNumber);
}
