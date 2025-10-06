package com.software.zero.pojo;

public class WebSocketMessageEvent {
    private String messageType;
    private String payloadJson;

    public WebSocketMessageEvent(String messageType, String payloadJson) {
        this.messageType = messageType;
        this.payloadJson = payloadJson;
    }

    public String getMessageType() {
        return messageType;
    }
    public String getPayloadJson() {
        return payloadJson;
    }
}