package com.software.zero.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessagePool {
    public static Queue<String> addFriendMessageQueue = new ConcurrentLinkedQueue<>();
    public static Queue<String> chatMessageQueue = new ConcurrentLinkedQueue<>();
}
