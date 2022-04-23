package com.example.helpers;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FileSystemHelper {
    private static final String FILE_OR_RESOURCE_NOT_FOUND = "File \"%s\" not found";

    private FileSystemHelper() {

    }

    public static String localFileNameOrResourceNameToFullPath(String fileOrResourceName) {
        String path = null;
        File file = new File(String.format("/%s", fileOrResourceName));
        if (file.exists()) {
            path = URLDecoder.decode(file.toURI().getPath(), StandardCharsets.UTF_8);
        }

        if (path == null) {
            path = Optional.ofNullable(FileSystemHelper.class.getClassLoader().getResource(fileOrResourceName))
                    .orElseThrow(() -> new RuntimeException(String.format(FILE_OR_RESOURCE_NOT_FOUND, fileOrResourceName))).toExternalForm();
        }

        return path;
    }
}
