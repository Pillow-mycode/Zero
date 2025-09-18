package com.software.zero;


import android.os.Bundle;

import com.software.zero.manager.PermissionManager;
import com.software.zero.view.MainBaseActivity;

public class MainActivity extends MainBaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 管理权限
        PermissionManager.handlePermission(this);
        // 初始化控件
        initPreStart();
        //创建地图
        mMapView.onCreate(savedInstanceState);
        // 配置控制器
        gaoDeMapService.configMapController(mMapView);
    }
    @Override
    public boolean openBluePointOrNot() {
        return true;
    }
}