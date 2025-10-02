package com.software.zero.ui.activity;


import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.MapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.websocket.WebSocketManager;
import com.software.zero.MyApp;
import com.software.zero.R;
import com.software.zero.ui.fragment.MainFragment;
import com.software.zero.ui.fragment.OursFragment;
import com.software.zero.ui.fragment.TalkFragment;
import com.software.zero.repository.UserRepository;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 主Activity，作为应用入口和Fragment容器
 * 使用底部导航栏管理三个主要Fragment的切换
 * @author Developer
 * @version 1.0
 * @since 2025-09-21
 */
public class MainActivity extends AppCompatActivity {

    // 日志标签
    private static final String TAG = "MainActivity";

    // 高德地图视图
    private MapView mMapView;

    // 当前显示的Fragment实例
    private Fragment currentFragment = null;

    // 应用的主要Fragment实例
    private final MainFragment mainFragment = MainFragment.newInstance();
    private final TalkFragment talkFragment = TalkFragment.newInstance();
    private final OursFragment oursFragment = OursFragment.newInstance();
    private EncryptedPrefsHelper encryptedPrefsHelper;
    
    // 用户数据仓库
    private UserRepository userRepository;

    /**
     * Activity创建时的生命周期方法
     * 初始化界面和底部导航栏
     * @param savedInstanceState 保存的实例状态Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();
        WebSocketManager webSocketManager = new WebSocketManager("ws://"+ MyApp.url +"/ws");
        webSocketManager.connect(encryptedPrefsHelper.getAuthToken(), webSocketManager.new MyListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }
        });
        // 设置内容视图为底部导航布局
        setContentView(R.layout.activity_bottom_navigation_view);
        
        // 初始化用户数据仓库
        userRepository = new UserRepository(this);

        handleMainFragment();


        // 初始化底部导航栏并设置项目选择监听器
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // 根据点击的导航项切换不同的Fragment
            if (itemId == R.id.navigation_home) {
                handleMainFragment();
                return true;
            } else if (itemId == R.id.navigation_message) {
                handleTalkFragment();
                return true;
            } else if (itemId == R.id.navigation_ours) {
                handleOursFragment();
                return true;
            }
            return false;
        });


    }

    /**
     * 处理并显示"我们的"Fragment
     * 使用add()和hide()而非replace()来保持Fragment状态
     */
    private void handleOursFragment() {
        // 获取FragmentManager并开始事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 先隐藏当前正在显示的Fragment（如果有）
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // 检查目标Fragment是否已添加过
        if (oursFragment.isAdded()) {
            // 已添加则直接显示
            fragmentTransaction.show(oursFragment);
        } else {
            // 未添加则添加到容器中并显示
            fragmentTransaction.add(R.id.fragment_container, oursFragment);
        }

        currentFragment = oursFragment; // 更新当前Fragment引用
        fragmentTransaction.commit(); // 提交事务
    }

    /**
     * 处理并显示"对话"Fragment
     */
    private void handleTalkFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (talkFragment.isAdded()) {
            fragmentTransaction.show(talkFragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, talkFragment);
        }
        currentFragment = talkFragment;
        fragmentTransaction.commit();
    }

    /**
     * 处理并显示"主页"Fragment
     */
    private void handleMainFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (mainFragment.isAdded()) {
            fragmentTransaction.show(mainFragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, mainFragment);
        }
        currentFragment = mainFragment;
        fragmentTransaction.commit();
    }
}