package com.example.config;

public class ServicerConfig {
    private static String ServicerAddress = "10.0.2.2";

    public static String getServicerAddress() {
        return ServicerAddress;
    }

    public static void setServicerAddress(String servicerAddress) {
        ServicerAddress = servicerAddress;
    }
}