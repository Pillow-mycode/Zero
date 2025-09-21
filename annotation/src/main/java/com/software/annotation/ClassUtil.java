package com.software.annotation;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {
    public static Class<?>[] getClasses(String packageName) {
        // 1. 获取类加载器
        ClassLoader classLoader = ClassUtil.class.getClassLoader();
        // 2. 将包名转换为路径
        String path = packageName.replace('.', '/');
        // 3. 获取该路径下的所有资源
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new RuntimeException("Package not found: " + packageName);
        }
        // 4. 列出该目录下的所有文件
        File directory = new File(resource.getFile());
        List<Class<?>> classes = new ArrayList<>();
        // 5. 遍历目录中的所有文件
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".class")) {
                        // 6. 加载类并添加到列表
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return classes.toArray(new Class[0]);
    }
}
