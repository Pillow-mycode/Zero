package com.software.util.share_preference;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenPrefsHelper {
    private final SharedPreferences encryptedSharedPreferences;
    private static volatile TokenPrefsHelper instance;
    public static void init(Application context) {
        if(instance == null)
            synchronized (TokenPrefsHelper.class) {
                if(instance == null)
                    instance = new TokenPrefsHelper(context);
        }
    }

    public static TokenPrefsHelper getInstance() {
        return instance;
    }

    private TokenPrefsHelper(Application context) {
        try {
            // 1. 创建或获取MasterKey
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // 2. 初始化EncryptedSharedPreferences
            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "now-token",
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

    public void saveString(String key, String value) {
        encryptedSharedPreferences.edit()
                .putString(key,value)
                .apply();
    }

    public String getString(String key) {
        return encryptedSharedPreferences.getString(key, null);
    }

    public void clearProject(String s) {
        encryptedSharedPreferences.edit().remove(s).apply();
    }
}
