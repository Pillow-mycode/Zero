package com.software.zero.view;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.MapView;
import com.software.zero.R;
import com.software.zero.service.GaoDeMapService;

import java.util.Objects;

public abstract class MainBaseActivity extends AppCompatActivity {
    protected MapView mMapView;
    protected GaoDeMapService gaoDeMapService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initPreStart() {
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        gaoDeMapService = new GaoDeMapService();
    }

    public abstract boolean openBluePointOrNot();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        String now = permissions[0];
        if(Objects.equals(now, Manifest.permission.ACCESS_FINE_LOCATION) && openBluePointOrNot()) gaoDeMapService.addBluePoint();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
