package com.software.zero.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.software.zero.dao.MessageDao;
import com.software.zero.dao.ChatDao;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.pojo.ChatHistory;
import com.software.zero.pojo.PeopleMessage;

@Database(entities = {AddFriendMessage.class, ChatHistory.class, PeopleMessage.class}, version = 9, exportSchema = false)
public abstract class ZeroDatabase extends RoomDatabase {
    public abstract MessageDao addFriendMessageDao();
    public abstract ChatDao chatDao();

    // 提供一个静态方法来获取指定用户的数据库实例
    public static ZeroDatabase getDatabase(final Context context, final String userId) {
        return Room.databaseBuilder(context.getApplicationContext(),
                        ZeroDatabase.class,
                        "room_database_" + userId + ".db") // 动态命名
                .fallbackToDestructiveMigration()
                .build();
    }
}