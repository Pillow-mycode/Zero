package com.software.zero;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.MapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.software.zero.manager.PermissionManager;
import com.software.zero.service.GaoDeMapService;
import com.software.zero.view.MainFragment;
import com.software.zero.view.OursFragment;
import com.software.zero.view.TalkFragment;

import java.util.Objects;

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

    /**
     * Activity创建时的生命周期方法
     * 初始化界面和底部导航栏
     * @param savedInstanceState 保存的实例状态Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置内容视图为底部导航布局
        setContentView(R.layout.activity_bottom_navigation_view);
        handleMainFragment();

        // 初始化底部导航栏并设置项目选择监听器
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "onCreate: 导航项点击: " + itemId);

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

    /**
     * 权限请求结果回调
     * 当用户响应权限请求时系统会调用此方法
     * @param requestCode 请求权限时传入的请求代码
     * @param permissions 请求的权限数组
     * @param grantResults 对应权限的授予结果
     * @param deviceId 设备ID（参数名称可能需确认）
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

        // 检查是否是精确定位权限的请求结果
        String now = permissions[0];
        if(Objects.equals(now, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "正在获取定位,请稍后...", Toast.LENGTH_SHORT).show();
            // 通知MainFragment定位权限已获授权
            mainFragment.onLocationPermissionAccept();
        }
    }
}