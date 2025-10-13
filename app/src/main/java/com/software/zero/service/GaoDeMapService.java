package com.software.zero.service;

import android.location.Location;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.software.zero.R;

public class GaoDeMapService {

    private AMap aMap;
    private OnLocationReceivedListener locationListener;
    private Marker avatarMarker; // 用于保存头像标记的引用
    private Marker girlAvatarMarker; // 用于保存女孩头像标记的引用
    private static final String TAG = "GaoDeMapService";

    // 定义位置接收监听器接口
    public interface OnLocationReceivedListener {
        void onLocationReceived(LatLng location);
    }

    public void setOnLocationReceivedListener(OnLocationReceivedListener listener) {
        this.locationListener = listener;
    }

    public void configMapController(MapView mapView) {
        if(aMap == null) {
            aMap = mapView.getMap();
            // 设置位置变化监听器
            aMap.setOnMyLocationChangeListener(location -> {
                if (location != null && locationListener != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    locationListener.onLocationReceived(latLng);
                }
            });
        }
    }
    
    public void enableLocation() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.interval(2000); 
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
    }

    public void setMapZoomTo100Meters(LatLng location) {
        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.5f));
        }
    }


    // 在新位置添加头像标记并移除旧标记
    public void updateAvatarMarker(LatLng location) {
        if (aMap != null) {
            // 移除旧的头像标记
            if (avatarMarker != null) {
                avatarMarker.remove();
            }
            
            // 添加新的头像标记
            if (location != null) {
                avatarMarker = aMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_boy))
                        .anchor(0.5f, 0.5f)); // 锚点设置在图片中心
            }
        }
    }
    
    public void updateGirlAvatarMarker(LatLng location) {
        if (aMap != null) {
                try {
                    // 强制移除所有可能存在的女孩头像标记
                    if (girlAvatarMarker != null) {
                        girlAvatarMarker.remove();
                    }
                    // 添加新的女孩头像标记
                    if (location != null) {
                        girlAvatarMarker = aMap.addMarker(new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_girl))
                                .anchor(0.5f, 0.5f)); // 锚点设置在图片中心
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
    
    // 移除头像标记
    public void removeAvatarMarker() {
        if (avatarMarker != null) {
            avatarMarker.remove();
            avatarMarker = null;
        }
    }

}