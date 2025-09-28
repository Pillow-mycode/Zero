package com.software.util.base64;

import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Base64ToFile {
    /**
     * 将base64字符串转换为临时文件
     * @param base64String base64编码的字符串
     * @param fileName 临时文件名
     * @return 临时文件对象
     * @throws IOException IO异常
     */
    public static File base64ToTempFile(String base64String, String fileName) throws IOException {
        // 去除base64字符串中的数据头(如: data:image/png;base64,)
        String base64Data = base64String;
        if (base64String.contains(",")) {
            base64Data = base64String.split(",")[1];
        }

        // 解码base64字符串
        byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);

        // 创建临时文件
        File tempFile = File.createTempFile("temp_", "_" + fileName);

        // 写入文件
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(decodedBytes);
        }

        return tempFile;
    }
}
