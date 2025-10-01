package com.software.login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.software.login.presenter.LoginContact;
import com.software.login.presenter.impl.LoginPresenterImpl;
import com.software.util.dialog.LoadingDialog;

public class LoginActivity extends AppCompatActivity implements LoginContact.LoginView{
    private static final String TAG = "LoginActivity";
    private  boolean isNowLogin = true;
    private  TextView tab_login;
    private TextView tab_register;
    private LinearLayout loginForm, registerForm;
    private View indicator;
    private EditText etLoginUsername, etLoginPassword;
    private EditText etRegisterUsername, etRegisterPassword, etConfirmPassword;
    private Button btnLogin, btnRegister;

    private LoginContact.LoginPresenter loginPresenter;
    private LoadingDialog loadingDialog;

    // 添加防抖时间间隔（毫秒）
    private static final long CLICK_DEBOUNCE_INTERVAL = 1000; // 1秒
    private long lastClickTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLayout();

        tab_login.setOnClickListener(v -> switchToLogin());
        tab_register.setOnClickListener(v -> switchToRegister());

        // 设置带防抖功能的登录和注册按钮点击事件
        btnLogin.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                performLogin();
            }
        });

        btnRegister.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                performRegister();
            }
        });
    }

    // 创建防抖点击监听器抽象类
    abstract class DebouncedOnClickListener implements View.OnClickListener {
        @Override
        public final void onClick(View v) {
            if (isFastDoubleClick()) {
                return;
            }
            onDebouncedClick(v);
        }

        public abstract void onDebouncedClick(View v);

        private boolean isFastDoubleClick() {
            long time = SystemClock.elapsedRealtime();
            if (time - lastClickTime < CLICK_DEBOUNCE_INTERVAL) {
                return true;
            }
            lastClickTime = time;
            return false;
        }
    }

    private void performLogin() {
        String username = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        loadingDialog.show();
        loginPresenter.login(username, password);
    }

    private void performRegister() {
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        loadingDialog.show();

        loginPresenter.register(username, password, confirmPassword);
    }

    // ... existing code ...

    private void switchToLogin() {
        if(!isNowLogin) {
            loginForm.setVisibility(VISIBLE);
            registerForm.setVisibility(GONE);
            animateTabIndicator(R.id.tab_login);

            isNowLogin = true;
        }
    }

    private void switchToRegister() {
        if(isNowLogin) {
            loginForm.setVisibility(GONE);
            registerForm.setVisibility(VISIBLE);
            animateTabIndicator(R.id.tab_register);

            isNowLogin = false;
        }
    }


    private void initLayout() {
        tab_login = findViewById(R.id.tab_login);
        tab_register = findViewById(R.id.tab_register);
        loginForm = findViewById(R.id.login_form);
        registerForm = findViewById(R.id.register_form);
        indicator = findViewById(R.id.tab_indicator);

        // 初始化登录表单控件
        etLoginUsername = findViewById(R.id.et_login_username);
        etLoginPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);

        // 初始化注册表单控件
        etRegisterUsername = findViewById(R.id.et_register_username);
        etRegisterPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        btnRegister = findViewById(R.id.btn_register_submit);

        loginPresenter = new LoginPresenterImpl(this);
        loadingDialog = new LoadingDialog(this);
    }

    // ... existing code ...

    private void animateTabIndicator(int tabId) {
        float targetX = (tabId == R.id.tab_login) ? 0 :
                getResources().getDisplayMetrics().widthPixels / 2f - indicator.getWidth() / 2f;

        ObjectAnimator animator = ObjectAnimator.ofFloat(indicator, "translationX", targetX);
        animator.setDuration(300);
        animator.start();
    }

    // ... existing code ...

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.dispatch();
    }

    // ... existing code ...

    @Override
    public void onUserNameEmpty() {
        Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onPasswordEmpty() {
        Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onUserNameLengthError() {
        Toast.makeText(this, "用户名长度应在5-15之间", Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onPasswordLengthError() {
        Toast.makeText(this, "密码长度应在5-15之间", Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onSuccess(String token) {
        Intent intent = new Intent();
        intent.putExtra("token_key", token);
        setResult(RESULT_OK, intent);
        loadingDialog.dismiss();
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onError(String message) {
        Log.d(TAG, "onError: " + message);
        Toast.makeText(this, "网络请求错误，请检查网络连接", Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }

    @Override
    public void onConfirmPasswordEmpty() {
        Toast.makeText(this, "请输入确认密码", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordNotMatch() {
        Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisteSuccess(String token) {
        Intent intent = new Intent();
        intent.putExtra("token_key", token);
        setResult(RESULT_OK, intent);
        loadingDialog.dismiss();
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRegisterFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        loadingDialog.dismiss();
    }
}
