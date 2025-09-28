package com.software.zero.config;

import com.software.login.config.ServicerBaseUrl;

public class ServicerConfig {
    private static String URL = ServicerBaseUrl.getURL();

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        ServicerConfig.URL = URL;
    }
}
