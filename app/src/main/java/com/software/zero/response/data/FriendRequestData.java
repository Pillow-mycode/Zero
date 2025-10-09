package com.software.zero.response.data;

public class FriendRequestData {
    private String profile_picture;
    private String phone_number;
    private String user_name;

    // 无参构造函数，供Gson反序列化使用
    public FriendRequestData() {
    }

    public FriendRequestData(String profile_picture, String phone_number, String user_name) {
        this.profile_picture = profile_picture;
        this.phone_number = phone_number;
        this.user_name = user_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}