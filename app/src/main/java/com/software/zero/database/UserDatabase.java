package com.software.zero.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.software.zero.dao.UserDao;
import com.software.zero.pojo.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static volatile UserDatabase INSTANCE;

    public static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "user_database")
                            .allowMainThreadQueries() // 仅用于测试，生产环境中应使用异步查询
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}