package com.software.zero.enums;

public enum WebSocketType {
    CHAT_MESSAGE("chat_message"),
    ADD_FRIEND("add_friend"),
    ACCEPT_FRIEND("accept_friend"),
    REJECT_FRIEND("reject_friend");

    private final String type;

    WebSocketType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static WebSocketType fromString(String text) {
        for (WebSocketType webSocketType : WebSocketType.values()) {
            if (webSocketType.type.equals(text)) {
                return webSocketType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}