package com.software.zero.enums;

public enum UserProperty {
    ID("id"),
    PROFILE_PICTURE("profilePicture"),
    USERNAME("username"),
    PHONE_NUMBER("phoneNumber"),
    THE_OTHER_ONE_PHONE_NUMBER("theOtherOnePhoneNumber"),
    THE_OTHER_ONE_ADD_TIME("theOtherOneAddTime");

    private final String propertyName;

    UserProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
