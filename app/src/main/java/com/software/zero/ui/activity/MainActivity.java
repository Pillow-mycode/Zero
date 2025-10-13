package com.software.zero.ui.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.MapView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.software.util.GsonUtil;
import com.software.util.address2file.Address2File;
import com.software.util.dialog.LoadingDialog;
import com.software.util.share_preference.EncryptedPrefsHelper;
import com.software.util.share_preference.TokenPrefsHelper;
import com.software.util.websocket.WebSocketHelper;
import com.software.zero.MyApp;
import com.software.zero.R;
import com.software.zero.enums.UserProperty;
import com.software.zero.enums.WebSocketType;
import com.software.zero.model.AddFriendModel;
import com.software.zero.pojo.AddFriendMessage;
import com.software.zero.pojo.ChatHistory;
import com.software.zero.pojo.PeopleMessage;
import com.software.zero.pojo.WebSocketMessageEvent;
import com.software.zero.repository.MessageRepository;
import com.software.zero.repository.ChatRepository;
import com.software.zero.response.data.FriendRequestData;
import com.software.zero.response.data.LocationData;
import com.software.zero.ui.fragment.CheckRefuseFragment;
import com.software.zero.ui.fragment.MainFragment;
import com.software.zero.ui.fragment.OursFragment;
import com.software.zero.ui.fragment.TalkFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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

    // 当前显示的Fragment实例
    private Fragment currentFragment = null;

    // 应用的主要Fragment实例
    private final MainFragment mainFragment = MainFragment.newInstance();
    private final TalkFragment talkFragment = TalkFragment.newInstance();
    private final OursFragment oursFragment = OursFragment.newInstance();
    private final CheckRefuseFragment checkRefuseFragment = CheckRefuseFragment.newInstance();
    private EncryptedPrefsHelper encryptedPrefsHelper;
    private TokenPrefsHelper tokenPrefsHelper;
    private WebSocketHelper webSocketHelper;
    private BottomNavigationView navigationView;
    private int messageCount = 0;
    private BadgeDrawable navigation_message;
    private BadgeDrawable navigation_add_friend;
    private boolean badgeStatus = false;
    private static MessageRepository messageRepository;
    private LoadingDialog dialog;
    private Disposable resumeDisposable;

    @Override
    protected void onResume() {
        super.onResume();
        resumeDisposable = io.reactivex.rxjava3.core.Single.fromCallable(() -> messageRepository.checkNewMessage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasNewMessage -> navigation_add_friend.setVisible(hasNewMessage),
                        throwable -> {
                            Log.e(TAG, "检查新消息失败", throwable);
                            navigation_add_friend.setVisible(false);
                        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (resumeDisposable != null && !resumeDisposable.isDisposed()) {
            resumeDisposable.dispose();
        }
    }

    /**
     * Activity创建时的生命周期方法
     * 初始化界面和底部导航栏
     * @param savedInstanceState 保存的实例状态Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleMainFragment();
        init();
        // 设置内容视图为底部导航布局
        setContentView(R.layout.activity_bottom_navigation_view);
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
                if(encryptedPrefsHelper.getString(UserProperty.PHONE_NUMBER.getPropertyName())!=null
                        && !encryptedPrefsHelper.getString(UserProperty.PHONE_NUMBER.getPropertyName()).isEmpty()) {
                    messageCount = 0;
                    updateBadges();
                    handleTalkFragment();
                    return true;
                }
                else {
                    handleCheckRefuseFragment();
                }

            } else if (itemId == R.id.navigation_ours) {
                handleOursFragment();
                return true;
            }
            return false;
        });

        setupBadges(); // 设立角标
    }


    private List<Disposable> disposable = new ArrayList<>();
    public void init() {
        EventBus.getDefault().register(this);
        tokenPrefsHelper = TokenPrefsHelper.getInstance();
        EncryptedPrefsHelper.init(MyApp.getInstance(), tokenPrefsHelper.getString("now-user"));
        encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();
        webSocketHelper = new WebSocketHelper("ws://" + MyApp.url + "/ws");
        webSocketHelper.connect(TokenPrefsHelper.getInstance().getAuthToken(), new WebSocketEventListenerImpl());
        messageRepository = new MessageRepository();
        dialog = new LoadingDialog(this);
        dialog.show();

        AddFriendModel model = new AddFriendModel();
        Disposable subscribe1 = model.findPeople(TokenPrefsHelper.getInstance().getString("now-user"))
                .subscribe(r -> {
                    if(r.isSuccess()) {
                        PeopleMessage peopleMessage = new PeopleMessage(r.getData().getProfile_picture(), r.getData().getPhone_number(), r.getData().getUser_name());
                        MyApp.setMyMessage(peopleMessage);

                        messageRepository.updatePeople(new PeopleMessage(r.getData().getProfile_picture(), r.getData().getPhone_number(), r.getData().getUser_name()));
                    }
                    else {
                        TokenPrefsHelper.getInstance().clearAuthToken();
                        tokenPrefsHelper.clearProject("now-user");
                        startActivity(new Intent(MainActivity.this, InterceptorActivity.class));
                        MainActivity.this.finish();
                    }
                }, e -> {
                    Toast.makeText(this, "获取本人信息失败", Toast.LENGTH_SHORT).show();
                });
        disposable.add(subscribe1);
        Disposable subscribe2 = model.findFriend()
                .subscribe(r -> {
                    if(r.isSuccess()) {
                        Address2File.preloadImage(this, r.getData().getProfile_picture());
                        encryptedPrefsHelper.saveString(UserProperty.PROFILE_PICTURE.getPropertyName(), r.getData().getProfile_picture());
                        encryptedPrefsHelper.saveString(UserProperty.USERNAME.getPropertyName(), r.getData().getUser_name());
                        encryptedPrefsHelper.saveString(UserProperty.PHONE_NUMBER.getPropertyName(), r.getData().getPhone_number());
                        PeopleMessage peopleMessage = new PeopleMessage(r.getData().getProfile_picture(), r.getData().getPhone_number(), r.getData().getUser_name());
                        MyApp.setTheOtherMessage(peopleMessage);
                        messageRepository.updatePeople(new PeopleMessage(r.getData().getProfile_picture(), r.getData().getPhone_number(), r.getData().getUser_name()));
                        dialog.dismiss();
                    }
                    else {
                        dialog.dismiss();
                        encryptedPrefsHelper.clearProject(UserProperty.PROFILE_PICTURE.getPropertyName());
                        encryptedPrefsHelper.clearProject(UserProperty.USERNAME.getPropertyName());
                        encryptedPrefsHelper.clearProject(UserProperty.PHONE_NUMBER.getPropertyName());
                        Log.d(TAG, "init: 暂无好友");
                    }
                }, e -> {
                    dialog.dismiss();
                    Log.d(TAG, "init: " + e.getMessage());
                    Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                });
        disposable.add(subscribe2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        for (Disposable disposable1 : disposable) {
            disposable1.dispose();
        }
        webSocketHelper.release();
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

    private void handleCheckRefuseFragment() {
        // 获取FragmentManager并开始事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 先隐藏当前正在显示的Fragment（如果有）
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // 检查目标Fragment是否已添加过
        if (checkRefuseFragment.isAdded()) {
            // 已添加则直接显示
            fragmentTransaction.show(checkRefuseFragment);
        } else {
            // 未添加则添加到容器中并显示
            fragmentTransaction.add(R.id.fragment_container, checkRefuseFragment);
        }

        currentFragment = checkRefuseFragment; // 更新当前Fragment引用
        fragmentTransaction.commit(); // 提交事务
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
        private EncryptedPrefsHelper encryptedPrefsHelper = EncryptedPrefsHelper.getInstance();
        private ChatRepository chatRepository = new ChatRepository();
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // 解析消息，获取类型
            try {
                JSONArray array = new JSONArray(text);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonMessage = new JSONObject(array.getString(i));
                    String messageType = jsonMessage.getString("type");
                    String payload = jsonMessage.getString("payload");  // 修复：使用getString而不是getJSONObject
                    if(messageType.equals(WebSocketType.LOCATION.getType())) {
                        Log.d("go", "onMessage: ===========================");
                        EventBus.getDefault().postSticky(new WebSocketMessageEvent(messageType, payload));
                    }
                    if(messageType.equals(WebSocketType.ADD_FRIEND.getType())) {
                        AddFriendMessage addFriendMessage = GsonUtil.fromJson(payload, AddFriendMessage.class);
                        addFriendMessage.setIsNew(1);
                        addFriendMessage.setHasRefuse(0);
                        // 在后台线程执行数据库操作
                        new Thread(() -> {
                            try {
                                messageRepository.insertMessage(addFriendMessage);
                            } catch (Exception e) {
                                Log.e(TAG, "插入好友请求失败", e);
                            }
                        }).start();
                    }
                    else if(messageType.equals(WebSocketType.CHAT_MESSAGE.getType())){
                        // 在后台线程执行数据库操作
                        new Thread(() -> {
                            try {
                                chatRepository.insertChat(new ChatHistory(payload, false));
                                // 在主线程发送事件
                                EventBus.getDefault().post(new WebSocketMessageEvent(messageType, payload));
                            } catch (Exception e) {
                                Log.e(TAG, "插入聊天记录失败", e);
                            }
                        }).start();
                    }
                    else if(messageType.equals(WebSocketType.ACCEPT_FRIEND.getType())) {
                        FriendRequestData data = GsonUtil.fromJson(payload, FriendRequestData.class);
                        Address2File.preloadImage(MyApp.getInstance(), data.getProfile_picture());
                        encryptedPrefsHelper.saveString(UserProperty.PROFILE_PICTURE.getPropertyName(), data.getProfile_picture());
                        encryptedPrefsHelper.saveString(UserProperty.USERNAME.getPropertyName(), data.getUser_name());
                        encryptedPrefsHelper.saveString(UserProperty.PHONE_NUMBER.getPropertyName(), data.getPhone_number());
                    }
                    else if(messageType.equals(WebSocketType.REJECT_FRIEND.getType())) {
                        FriendRequestData data = GsonUtil.fromJson(payload, FriendRequestData.class);
                        encryptedPrefsHelper.clearProject(data.getPhone_number());
                    }
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