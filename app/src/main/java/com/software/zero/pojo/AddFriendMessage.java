package com.software.zero.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "add_friend_message")
public class AddFriendMessage {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String profile_picture;
    private String user_name;
    private String phone_umber;
    private int isNew;
    private int hasRefuse;

    public AddFriendMessage(int id, String profile_picture, String user_name, String phone_umber, int isNew, int hasRefuse) {
        this.id = id;
        this.profile_picture = profile_picture;
        this.user_name = user_name;
        this.phone_umber = phone_umber;
        this.isNew = isNew;
        this.hasRefuse = hasRefuse;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_umber() {
        return phone_umber;
    }

    public void setPhone_umber(String phone_umber) {
        this.phone_umber = phone_umber;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getHasRefuse() {
        return hasRefuse;
    }

    public void setHasRefuse(int hasRefuse) {
        this.hasRefuse = hasRefuse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
