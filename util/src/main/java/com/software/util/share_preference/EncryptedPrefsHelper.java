package com.software.util.share_preference;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedPrefsHelper {
    private static final String PREFS_NAME = "secure_auth_prefs";
    private final SharedPreferences encryptedSharedPreferences;
    private static volatile EncryptedPrefsHelper instance;
    public static void init(Application context) {
        if(instance == null) {
            synchronized (EncryptedPrefsHelper.class) {
                if(instance == null) {
                    instance = new EncryptedPrefsHelper(context);
                }
            }
        }
    }

    public static EncryptedPrefsHelper getInstance() {
        return instance;
    }

    public String getString(String key) {
        return encryptedSharedPreferences.getString(key, null);
    }



    private EncryptedPrefsHelper(Application context) {
        try {
            // 1. 创建或获取MasterKey
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // 2. 初始化EncryptedSharedPreferences
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EncryptedSharedPreferences", e);
        }
    }

    // 保存Token
    public void saveAuthToken(String token) {
        encryptedSharedPreferences.edit()
                .putString("auth_token", token)
                .apply(); // 异步写入，不会阻塞。也可以用 commit() 同步写入。
    }

    public void saveBoolean(String key, Boolean b) {
        encryptedSharedPreferences.edit()
                .putBoolean(key, b)
                .apply();;
    }

    public Boolean getBoolean(String key) {
        return encryptedSharedPreferences.getBoolean(key, false);
    }

    // 获取Token
    public String getAuthToken() {
        return encryptedSharedPreferences.getString("auth_token", null);
    }

    // 清除Token
    public void clearAuthToken() {
        encryptedSharedPreferences.edit()
                .remove("auth_token")
                .apply();
    }
}
