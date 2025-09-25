package com.software.zero.config;
public class ServicerConfig {
    private static String URL = "http://192.168.91.86:8080";

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        ServicerConfig.URL = URL;
    }
}
