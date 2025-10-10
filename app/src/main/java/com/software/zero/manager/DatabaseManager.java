package com.software.zero.manager;

import android.content.Context;

import com.software.zero.database.ZeroDatabase;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static Map<String, ZeroDatabase> databaseInstances = new HashMap<>();

    public static ZeroDatabase getDatabase(Context context, String userId) {
        // 如果 map 中已有该用户的实例，直接返回
        if (databaseInstances.containsKey(userId)) {
            return databaseInstances.get(userId);
        }
        // 如果没有，则创建并缓存
        ZeroDatabase newDatabase = ZeroDatabase.getDatabase(context, userId);
        databaseInstances.put(userId, newDatabase);
        return newDatabase;
    }

    public static void closeDatabase(String userId) {
        if (databaseInstances.containsKey(userId)) {
            databaseInstances.get(userId).close();
            databaseInstances.remove(userId);
        }
    }
}