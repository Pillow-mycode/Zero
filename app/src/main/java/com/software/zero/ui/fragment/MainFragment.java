package com.software.zero.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.MapView;
import com.software.zero.R;
import com.software.zero.manager.PermissionManager;
import com.software.zero.service.GaoDeMapService;

public class MainFragment extends Fragment {
    private MapView mMapView;
    private GaoDeMapService gaoDeMapService;

    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化控件
        initPreStart(view);

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }

        // 配置控制器
        if (gaoDeMapService != null) {
            gaoDeMapService.configMapController(mMapView); // 修正方法调用
        }

        if (PermissionManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getContext(), "已获取定位权限", Toast.LENGTH_SHORT).show();
            if (gaoDeMapService != null) {
                gaoDeMapService.addBluePoint();
            }
        }
    }


    private void initPreStart(View view) {
        //获取地图控件引用
        mMapView = view.findViewById(R.id.map);
        gaoDeMapService = new GaoDeMapService();
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }
}
