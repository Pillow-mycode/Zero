package com.software.zero.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "users")
@TypeConverters(User.LocalDateTimeConverter.class)
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String profilePicture;
    private String username;
    private String phoneNumber;
    private String theOtherOnePhoneNumber;
    private LocalDateTime theOtherOneAddTime;
    
    // Constructors
    public User() {}
    
    public User(String profilePicture, String username, String phoneNumber, 
                String theOtherOnePhoneNumber, LocalDateTime theOtherOneAddTime) {
        this.profilePicture = profilePicture;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.theOtherOnePhoneNumber = theOtherOnePhoneNumber;
        this.theOtherOneAddTime = theOtherOneAddTime;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getTheOtherOnePhoneNumber() {
        return theOtherOnePhoneNumber;
    }
    
    public void setTheOtherOnePhoneNumber(String theOtherOnePhoneNumber) {
        this.theOtherOnePhoneNumber = theOtherOnePhoneNumber;
    }
    
    public LocalDateTime getTheOtherOneAddTime() {
        return theOtherOneAddTime;
    }
    
    public void setTheOtherOneAddTime(LocalDateTime theOtherOneAddTime) {
        this.theOtherOneAddTime = theOtherOneAddTime;
    }
    
    // TypeConverter for Room database
    public static class LocalDateTimeConverter {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        @TypeConverter
        public static LocalDateTime fromString(String value) {
            return value == null ? null : LocalDateTime.parse(value, formatter);
        }
        
        @TypeConverter
        public static String toString(LocalDateTime localDateTime) {
            return localDateTime == null ? null : localDateTime.format(formatter);
        }
    }
}