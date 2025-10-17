package com.software.router;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Router {
    public static Map<String, Class<?>> map = new HashMap<>();

    public static void register(final String path, final Class<?> clazz) {
        synchronized (Router.class) {
            map.put(path, clazz);
        }
    }

    public static void navigate(final Context context, final String path) {
        synchronized (Router.class) {
            context.startActivity(new Intent(context, map.get(path)));
        }
    }
}
