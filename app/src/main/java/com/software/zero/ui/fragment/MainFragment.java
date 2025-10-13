package com.software.zero.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.software.util.GsonUtil;
import com.software.util.retrofit.MyRetrofit;
import com.software.zero.R;
import com.software.zero.api.SendLocation;
import com.software.zero.enums.WebSocketType;
import com.software.zero.manager.PermissionManager;
import com.software.zero.pojo.Location;
import com.software.zero.pojo.WebSocketMessageEvent;
import com.software.zero.response.data.LocationData;
import com.software.zero.service.GaoDeMapService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainFragment extends Fragment implements AMapLocationListener {
    private static final String TAG = "Main";
    private MapView mMapView;
    private GaoDeMapService gaoDeMapService;
    private boolean isLocationReceived = false;
    private LatLng currentLocation = null;

    private ScheduledExecutorService scheduler;
    private volatile boolean isSending = false;
    // 声明AMapLocationClient和AMapLocationClientOption对象
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private MyRetrofit retrofit = MyRetrofit.getInstance();
    private Disposable subscribe = null;



    // 停止定时任务
    private void stopLocationSending() {
        isSending = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }


    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        Log.d("go", "EventBus: =============================");
        
        // 初始化控件
        initPreStart(view);

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }

        // 配置控制器
        if (gaoDeMapService != null) {
            gaoDeMapService.configMapController(mMapView);
            
            // 设置位置接收监听器
            gaoDeMapService.setOnLocationReceivedListener(new GaoDeMapService.OnLocationReceivedListener() {
                @Override
                public void onLocationReceived(LatLng location) {
                    // 更新currentLocation变量，以便发送到服务器

                    if (location != null) {
                        currentLocation = location;
                        Log.i("LocationChange", "位置已更新: " + location.latitude + ", " + location.longitude);
                    }
                    
                    // 只在第一次接收到位置时执行操作
                    if (!isLocationReceived && location != null) {
                        isLocationReceived = true;
                        
                        // 设置地图缩放到100米范围
                        gaoDeMapService.setMapZoomTo100Meters(currentLocation);
                    }
                    // 更新头像标记位置
                    if (location != null) {
                        gaoDeMapService.updateAvatarMarker(location);
                    }
                }
            });
        }

        if (gaoDeMapService != null) {
            gaoDeMapService.enableLocation();
        }
        
        // 处理可能存在的粘性事件
        WebSocketMessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(WebSocketMessageEvent.class);
        if (stickyEvent != null && stickyEvent.getMessageType().equals(WebSocketType.LOCATION.getType())) {
            // 处理粘性事件
            onEvent(stickyEvent);
            // 移除粘性事件
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        try {
            // 初始化定位客户端
            mLocationClient = new AMapLocationClient(requireContext().getApplicationContext());
            // 设置定位监听器
            mLocationClient.setLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        // 设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        // ！！！核心配置：设置为非单次定位（即持续定位）
        mLocationOption.setOnceLocation(false);

        // ！！！核心配置：启用传感器，允许SDK使用传感器感知设备是否移动
        mLocationOption.setSensorEnable(true);

        // 设置是否需要地址信息
        mLocationOption.setNeedAddress(true);

        // 给定位客户端设置参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    // ！！！当位置发生改变时，SDK会自动调用此方法
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            // 定位成功，获取最新坐标
            double lat = aMapLocation.getLatitude();
            double lon = aMapLocation.getLongitude();
            Log.i("LocationChange", "位置已更新: " + lat + ", " + lon);
            // 在这里处理新的位置坐标，例如更新UI、上传服务器、绘制轨迹等


            SendLocation sendLocation = retrofit.create(SendLocation.class);

            subscribe = sendLocation.sendLocation(lat, lon)
                    .subscribe(r -> {}, e -> {});
        } else {
            // 处理错误
            Log.e("AmapError", "定位失败, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketMessageEvent event) {
        String type = event.getMessageType();
        Log.d(TAG, "onEvent: " + type);
        if(type.equals(WebSocketType.LOCATION.getType())) {
            String payloadJson = event.getPayloadJson();
            Log.d(TAG, "onEvent: " + payloadJson);
            LocationData data = GsonUtil.fromJson(payloadJson, LocationData.class);
            // 假设您从某个地方获取到了另一个用户的位置信息
            LatLng girlLocation = new LatLng(data.getLatitude(), data.getLongitude());
            if (gaoDeMapService != null) {
                gaoDeMapService.updateGirlAvatarMarker(girlLocation);
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
        // 启动定位
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
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
        // 停止定位以省电
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // 移除可能存在的粘性事件
        EventBus.getDefault().removeStickyEvent(WebSocketMessageEvent.class);
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        stopLocationSending();
        
        // 移除头像标记
        if (gaoDeMapService != null) {
            gaoDeMapService.removeAvatarMarker();
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