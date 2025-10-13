package com.example.config;

public class ServicerConfig {
    private static String ServicerAddress = "192.168.70.86";

    public static String getServicerAddress() {
        return ServicerAddress;
    }

    public static void setServicerAddress(String servicerAddress) {
        ServicerAddress = servicerAddress;
    }
}