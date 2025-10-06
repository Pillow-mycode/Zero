package com.software.zero.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.software.zero.MyApp;
import com.software.zero.dao.AddFriendMessageDao;
import com.software.zero.pojo.AddFriendMessage;

@Database(entities = {AddFriendMessage.class}, version = 2, exportSchema = false)
public abstract class ZeroDatabase extends RoomDatabase {
    public abstract AddFriendMessageDao addFriendMessageDao();

    private static volatile ZeroDatabase INSTANCE;

    public static ZeroDatabase getDatabase() {
        if (INSTANCE == null) {
            synchronized (ZeroDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MyApp.getInstance(),
                                    ZeroDatabase.class, "zero_database")
                            .fallbackToDestructiveMigration() // 破坏性迁移
                            .allowMainThreadQueries() // 仅用于测试，生产环境中应使用异步查询
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}