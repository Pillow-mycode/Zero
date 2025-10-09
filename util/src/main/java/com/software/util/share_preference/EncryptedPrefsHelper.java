package com.software.util.share_preference;


import android.app.Application;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedPrefsHelper {
    private final SharedPreferences encryptedSharedPreferences;
    private static volatile EncryptedPrefsHelper instance;
    public static void init(Application context, String nowUser) {
        if(instance == null)
            synchronized (EncryptedPrefsHelper.class) {
                if (instance == null)
                    instance = new EncryptedPrefsHelper(context, nowUser);
            }
    }

    public static EncryptedPrefsHelper getInstance() {
        return instance;
    }

    public String getString(String key) {
        return encryptedSharedPreferences.getString(key, null);
    }



    private EncryptedPrefsHelper(Application context, String nowUser) {
        try {
            // 1. 创建或获取MasterKey
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // 2. 初始化EncryptedSharedPreferences
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    nowUser,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EncryptedSharedPreferences", e);
        }
    }
    public void saveBoolean(String key, Boolean b) {
        encryptedSharedPreferences.edit()
                .putBoolean(key, b)
                .apply();
    }

    public void saveString(String key, String value) {
        encryptedSharedPreferences.edit()
                .putString(key,value)
                .apply();
    }

    public Boolean getBoolean(String key) {
        return encryptedSharedPreferences.getBoolean(key, false);
    }

    public void clearProject(String key) {
        encryptedSharedPreferences.edit()
                .remove(key)
                .apply();
    }
}
