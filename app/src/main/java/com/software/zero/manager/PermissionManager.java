package com.software.zero.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import com.amap.api.maps.MapsInitializer;
import com.amap.apis.utils.core.api.AMapUtilCoreApi;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    public static Long permissionLock;
    private PermissionManager() { }
    // 存储所有需要的权限
    private static final Map<String, Boolean> allTheRequest = new HashMap<>();

    // 存储权限
    static {
        allTheRequest.put(Manifest.permission.ACCESS_FINE_LOCATION, false);
    }

    // 一次性处理所有权限
    public static void handlePermission(Activity activity) {
        MapsInitializer.updatePrivacyShow(activity,true,true);
        MapsInitializer.updatePrivacyAgree(activity,true);
        AMapUtilCoreApi.setCollectInfoEnable(true);

        allTheRequest.forEach((permission, isAllowed) -> {
            activity.requestPermissions(new String[]{permission}, 0);
            // 更新状态
            allTheRequest.replace(permission ,activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        });

    }
}
