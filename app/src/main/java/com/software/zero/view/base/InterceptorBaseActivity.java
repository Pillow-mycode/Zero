package com.software.zero.view.base;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.software.util.EncryptedPrefsHelper;
import com.software.zero.R;
import com.software.zero.manager.PermissionManager;

public abstract class InterceptorBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize_page);
        PermissionManager.handlePermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //startActivity(new Intent(this, MainActivity.class));
        onIntercept();
    }

    protected abstract void onIntercept();


}
