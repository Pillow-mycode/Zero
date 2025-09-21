package com.software.annotation;

public class MapperScanUtil {
    public static void scan(String packageName) {
        // 扫描对应包下的类
        Class<?>[] classes = ClassUtil.getClasses(packageName);
    }
}
