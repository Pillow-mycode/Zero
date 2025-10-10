package com.software.zero.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.pojo.PeopleMessage;

import java.util.List;

@Dao
public interface MessageDao {
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

    @Query("UPDATE `people-message` SET profile_picture = :profilePicture & user_name = :userName WHERE phone_number = :phoneNumber")
    void updatePeople(String profilePicture, String phoneNumber, String userName);

    @Query("SELECT phone_number FROM `people-message` WHERE phone_number = :phoneNumber")
    String findAllPeople(String phoneNumber);

    @Query("INSERT INTO `people-message` (profile_picture, phone_number, user_name) VALUES (:profilePicture, :phoneNumber, :userName)")
    void insertPeople(String profilePicture, String phoneNumber, String userName);

    @Query("SELECT * FROM `people-message` WHERE phone_number = :phoneNumber")
    List<PeopleMessage> findPeopleMessage(String phoneNumber);
}