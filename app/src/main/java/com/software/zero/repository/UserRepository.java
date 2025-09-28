package com.software.zero.repository;

import android.content.Context;

import com.software.zero.database.UserDatabase;
import com.software.zero.dao.UserDao;
import com.software.zero.pojo.User;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private UserDatabase db;

    public UserRepository(Context context) {
        db = UserDatabase.getDatabase(context);
        userDao = db.userDao();
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(User user) {
        userDao.deleteUser(user);
    }

    public void deleteAllUsers() {
        userDao.deleteAllUsers();
    }
}