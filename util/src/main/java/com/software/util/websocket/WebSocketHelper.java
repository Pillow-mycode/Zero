package com.software.util.websocket;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketHelper {
    private static final String TAG = "WebSocketHelper";
    private static final long RECONNECT_INTERVAL = 5000; // 重连间隔5秒
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // 最大重连次数

    private String serverUrl;
    private String token;
    private WebSocket webSocket;
    private OkHttpClient client;
    private boolean isConnected = false;
    private int reconnectAttempts = 0;
    private final Gson gson = new Gson();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private boolean useSecureProtocol = false; // 是否使用安全协议

    private WebSocketEventListener eventListener;

    /**
     * 构造函数
     * @param serverUrl WebSocket服务器地址
     */
    public WebSocketHelper(String serverUrl) {
        this(serverUrl, false);
    }

    /**
     * 构造函数
     * @param serverUrl WebSocket服务器地址
     * @param useSecureProtocol 是否使用安全协议(true:wss://, false:ws://)
     */
    public WebSocketHelper(String serverUrl, boolean useSecureProtocol) {
        this.useSecureProtocol = useSecureProtocol;
        this.serverUrl = adjustProtocol(serverUrl);
        this.client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .pingInterval(30, TimeUnit.SECONDS) // 每30秒发送一次ping
                .build();
    }

    /**
     * 调整协议
     * @param url 原始URL
     * @return 调整后的URL
     */
    private String adjustProtocol(String url) {
        if (!useSecureProtocol && url.startsWith("wss://")) {
            return url.replaceFirst("wss://", "ws://");
        } else if (useSecureProtocol && url.startsWith("ws://") && !url.startsWith("wss://")) {
            return url.replaceFirst("ws://", "wss://");
        }
        return url;
    }

    /**
     * WebSocket事件监听接口
     */
    public interface WebSocketEventListener {
        void onOpen(WebSocket webSocket, Response response);
        void onMessage(WebSocket webSocket, String text);
        void onMessage(WebSocket webSocket, ByteString bytes);
        void onClosing(WebSocket webSocket, int code, String reason);
        void onClosed(WebSocket webSocket, int code, String reason);
        void onFailure(WebSocket webSocket, Throwable t, Response response);
    }

    /**
     * 连接到WebSocket服务器
     * @param token 认证token
     * @param listener 事件监听器
     */
    public synchronized void connect(String token, WebSocketEventListener listener) {
        this.token = token;
        this.eventListener = listener;

        // 如果已连接，先断开
        if (isConnected && webSocket != null) {
            webSocket.close(1000, "Reconnecting");
        }

        // 构建带认证头的请求
        Request request = new Request.Builder()
                .url(serverUrl)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // 在后台线程执行连接
        executorService.execute(() -> {
            try {
                this.webSocket = client.newWebSocket(request, createWebSocketListener());
                Log.d(TAG, "正在连接到WebSocket: " + serverUrl);
            } catch (Exception e) {
                Log.e(TAG, "WebSocket连接异常", e);
                if (eventListener != null) {
                    eventListener.onFailure(null, e, null);
                }
                scheduleReconnect();
            }
        });
    }

    /**
     * 创建WebSocket监听器
     */
    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                isConnected = true;
                reconnectAttempts = 0;
                Log.d(TAG, "WebSocket连接已建立");

                if (eventListener != null) {
                    eventListener.onOpen(webSocket, response);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "onMessage: " + "接收到信息：" + text);
                if (eventListener != null) {
                    eventListener.onMessage(webSocket, text);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                Log.d(TAG, "收到二进制消息，长度: " + bytes.size());

                if (eventListener != null) {
                    eventListener.onMessage(webSocket, bytes);
                }
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "WebSocket正在关闭: " + code + " - " + reason);
                isConnected = false;

                if (eventListener != null) {
                    eventListener.onClosing(webSocket, code, reason);
                }
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "WebSocket已关闭: " + code + " - " + reason);
                isConnected = false;

                if (eventListener != null) {
                    eventListener.onClosed(webSocket, code, reason);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e(TAG, "WebSocket连接失败", t);
                isConnected = false;

                if (eventListener != null) {
                    eventListener.onFailure(webSocket, t, response);
                }

                // 尝试重连
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    scheduleReconnect();
                } else {
                    Log.e(TAG, "已达到最大重连次数，停止重连");
                    reconnectAttempts = 0;
                }
            }
        };
    }

    /**
     * 安排重连
     */
    private void scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "已达到最大重连次数，停止重连");
            return;
        }

        reconnectAttempts++;
        long delay = RECONNECT_INTERVAL * reconnectAttempts; // 指数退避策略

        Log.w(TAG, String.format("计划在%d毫秒后重连(%d/%d)", delay, reconnectAttempts, MAX_RECONNECT_ATTEMPTS));

        scheduledExecutorService.schedule(() -> {
            if (token != null && eventListener != null) {
                Log.d(TAG, "执行重连...");
                connect(token, eventListener);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送消息到WebSocket服务器
     * @param type 消息类型
     * @param content 消息内容
     * @return 是否成功发送
     */
    public synchronized boolean sendMessage(String type, String content) {
        if (webSocket == null || !isConnected) {
            Log.w(TAG, "WebSocket未连接，无法发送消息");
            return false;
        }

        try {
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("type", type);
            messageMap.put("content", content);

            String messageJson = gson.toJson(messageMap);

            // 在后台线程发送消息
            executorService.execute(() -> {
                try {
                    webSocket.send(messageJson);
                    Log.d(TAG, "消息已发送: " + messageJson);
                } catch (Exception e) {
                    Log.e(TAG, "发送消息失败", e);
                }
            });

            return true;
        } catch (Exception e) {
            Log.e(TAG, "构建消息失败", e);
            return false;
        }
    }

    /**
     * 发送原始文本消息
     * @param message 消息内容
     * @return 是否成功发送
     */
    public synchronized boolean sendRawMessage(String message) {
        if (webSocket == null || !isConnected) {
            Log.w(TAG, "WebSocket未连接，无法发送消息");
            return false;
        }

        executorService.execute(() -> {
            try {
                webSocket.send(message);
                Log.d(TAG, "原始消息已发送: " + message);
            } catch (Exception e) {
                Log.e(TAG, "发送原始消息失败", e);
            }
        });

        return true;
    }

    /**
     * 断开WebSocket连接
     */
    public synchronized void disconnect() {
        Log.d(TAG, "断开WebSocket连接");

        if (webSocket != null) {
            executorService.execute(() -> {
                try {
                    webSocket.close(1000, "正常关闭");
                } catch (Exception e) {
                    Log.e(TAG, "关闭WebSocket时出错", e);
                } finally {
                    webSocket = null;
                    isConnected = false;
                    reconnectAttempts = 0;
                }
            });
        }

        // 取消所有计划的重连
        scheduledExecutorService.shutdownNow();
    }

    /**
     * 检查WebSocket是否已连接
     * @return 连接状态
     */
    public synchronized boolean isConnected() {
        return isConnected && webSocket != null;
    }

    /**
     * 获取服务器URL
     * @return 服务器URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * 获取当前token
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * 释放资源
     */
    public void release() {
        disconnect();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}