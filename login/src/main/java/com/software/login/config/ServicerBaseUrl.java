package com.software.login.config;

public class ServicerBaseUrl {
    private static String URL = "http://192.168.91.86:8080";

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        ServicerBaseUrl.URL = URL;
    }
}
