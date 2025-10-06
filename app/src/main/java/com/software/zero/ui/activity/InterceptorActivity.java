package com.software.zero.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.software.login.LoginActivity;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.dialog.LoadingDialog;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.zero.contract.InterceptorContract;
import com.software.zero.presenter.InterceptorPresenter;
import com.software.zero.ui.activity.base.InterceptorBaseActivity;

public class InterceptorActivity extends InterceptorBaseActivity implements InterceptorContract.View {
    private TokenPrefsHelper tokenPrefsHelper;
    private ActivityResultLauncher<Intent> startActivityLauncher;
    private InterceptorContract.Presenter presenter;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new InterceptorPresenter(this);
        tokenPrefsHelper = TokenPrefsHelper.getInstance();
        loadingDialog = new LoadingDialog(this);
        
        // 在 onCreate 中注册 ActivityResultLauncher
        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String token = data.getStringExtra("token_key");
                            if (token != null && !token.isEmpty()) {
                                // 保存token
                                tokenPrefsHelper.saveAuthToken(token);
                            }
                            startActivity(new Intent(InterceptorActivity.this, MainActivity.class));
                            finish();
                        } else {
                            throw new RuntimeException("data 为空");
                        }
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    @Override
    protected void onIntercept() {
        String authToken = tokenPrefsHelper.getAuthToken();
        if(authToken == null || authToken.isEmpty()) {
            // 启动登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityLauncher.launch(intent);
        } else {
            loadingDialog.show();
            presenter.checkTheTokenEffect(authToken);
        }
    }

    @Override
    public void onTokenAccept() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onTokenWrong() {
        Toast.makeText(this, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
        // 启动登录界面
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityLauncher.launch(intent);
    }

    @Override
    public void onVisitServerError() {
        Toast.makeText(this, "网络错误, 请检查网络连接", Toast.LENGTH_SHORT).show();
        // 启动登录界面
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityLauncher.launch(intent);
    }
}