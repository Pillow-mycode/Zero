package com.software.login.config;

public class ServicerBaseUrl {
    private static String URL = "http://10.0.2.2:8080";

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        ServicerBaseUrl.URL = URL;
    }
}
