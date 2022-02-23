package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    /**
     * Получение массива байтов из потока
     * @param inputStream поток для обработки
     * */
    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(inputStream.available());

        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bs.write(data, 0, nRead);
        }

        return bs.toByteArray();
    }
}
