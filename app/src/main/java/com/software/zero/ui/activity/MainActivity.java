package com.software.zero.ui.activity;


import android.graphics.Color;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.MapView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.software.util.GsonUtil;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.util.websocket.WebSocketHelper;
import com.software.zero.MyApp;
import com.software.zero.R;
import com.software.zero.enums.WebSocketType;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.pojo.WebSocketMessageEvent;
import com.software.zero.repository.AddFriendRepository;
import com.software.zero.ui.fragment.MainFragment;
import com.software.zero.ui.fragment.OursFragment;
import com.software.zero.ui.fragment.TalkFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.Response;
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
    private TokenPrefsHelper tokenPrefsHelper;
    private WebSocketHelper webSocketHelper;
    private BottomNavigationView navigationView;
    private int messageCount = 0;
    private BadgeDrawable navigation_message;
    private BadgeDrawable navigation_add_friend;
    private boolean badgeStatus = false;
    private static AddFriendRepository addFriendRepository;


    /**
     * Activity创建时的生命周期方法
     * 初始化界面和底部导航栏
     * @param savedInstanceState 保存的实例状态Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // 设置内容视图为底部导航布局
        setContentView(R.layout.activity_bottom_navigation_view);

        handleMainFragment();
        // 初始化底部导航栏并设置项目选择监听器
        navigationView = findViewById(R.id.bottom_navigation);
        navigation_add_friend = navigationView.getOrCreateBadge(R.id.navigation_ours);
        navigation_add_friend.setBackgroundColor(Color.RED);


        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // 根据点击的导航项切换不同的Fragment
            if (itemId == R.id.navigation_home) {
                handleMainFragment();
                return true;
            } else if (itemId == R.id.navigation_message) {
                messageCount = 0;
                updateBadges();
                handleTalkFragment();
                return true;
            } else if (itemId == R.id.navigation_ours) {
                handleOursFragment();
                return true;
            }
            return false;
        });

        setupBadges(); // 设立角标
    }

    public void init() {
        EventBus.getDefault().register(this);
        tokenPrefsHelper = TokenPrefsHelper.getInstance();
        EncryptedPrefsHelper.init(MyApp.getInstance(), tokenPrefsHelper.getString("now-user"));
        encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();
        webSocketHelper = new WebSocketHelper("ws://" + MyApp.url + "/ws");
        webSocketHelper.connect(TokenPrefsHelper.getInstance().getAuthToken(), new WebSocketEventListenerImpl());
        addFriendRepository = new AddFriendRepository();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(addFriendRepository.checkNewMessage()) {
            navigation_add_friend.setVisible(true);
        } else {
            navigation_add_friend.setVisible(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void countMessage(WebSocketMessageEvent event) {
        if(Objects.equals(event.getMessageType(), WebSocketType.CHAT_MESSAGE.getType())) {
            messageCount++;
            updateBadges();
        }
    }

    private void updateBadges() {
        if(messageCount > 0) {
            navigation_message.setNumber(messageCount);
            if(!badgeStatus){
                navigation_message.setVisible(true);
                badgeStatus = true;
            }
        }
        else {
            navigation_message.setVisible(false);
            badgeStatus = false;
        }
    }

    private void setupBadges() {
        navigation_message = navigationView.getOrCreateBadge(R.id.navigation_message);
        navigation_message.setBackgroundColor(Color.RED);
        navigation_message.setBadgeTextColor(Color.WHITE);
        navigation_message.setNumber(messageCount); // 设置角标数字，超过99会显示99+
        navigation_message.setMaxCharacterCount(3); // 设置最大字符数
        navigation_message.setVisible(false);
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

    public static class WebSocketEventListenerImpl implements WebSocketHelper.WebSocketEventListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // 解析消息，获取类型
            try {
                JSONObject jsonMessage = new JSONObject(text);
                String messageType = jsonMessage.getString("type");
                String payload = jsonMessage.getString("payload");  // 修复：使用getString而不是getJSONObject

                if(messageType.equals(WebSocketType.ADD_FRIEND.getType())) {
                    AddFriendMessage addFriendMessage = GsonUtil.fromJson(payload, AddFriendMessage.class);
                    addFriendMessage.setIsNew(1);
                    addFriendMessage.setHasRefuse(0);
                    addFriendRepository.insertMessage(addFriendMessage);
                }
                else {
                    // 创建一个通用事件，包含类型和数据
                    WebSocketMessageEvent event = new WebSocketMessageEvent(messageType, payload);
                    EventBus.getDefault().post(event);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        }

    }
}