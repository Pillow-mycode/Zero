package com.software.login.config;

import com.example.config.ServicerConfig;

public class ServicerBaseUrl {
    private static String URL = "http://" + ServicerConfig.getServicerAddress() + ":8080";

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        ServicerBaseUrl.URL = URL;
    }
}
