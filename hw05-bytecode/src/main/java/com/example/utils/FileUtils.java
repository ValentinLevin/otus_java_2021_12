package com.example.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {
    private FileUtils() {

    }

    /**
     * Получение списка файлов скомпилированных классов
     * @param classLoader Загрузчик, в котором производится поиск классов
     * @param scanFromPackage Начиная с какого пакета искать классы
     * */
    public static Collection<String> getClassNamesFromPackage(
            ClassLoader classLoader, String scanFromPackage) throws IOException, URISyntaxException
    {
        Collection<String> classFileNames = new ArrayList<>();

        scanFromPackage = scanFromPackage.replaceAll("[.]", "/");
        URL packageURL = classLoader.getResource(scanFromPackage);

        if (packageURL != null) {
            if (packageURL.getProtocol().equals("jar")) {
                String jarFileName = URLDecoder.decode(packageURL.getFile(), StandardCharsets.UTF_8);
                jarFileName = jarFileName.substring(".jar!".length(), jarFileName.indexOf("!"));
                return getClassFilesFromJar(jarFileName, scanFromPackage);
            } else {
                return getClassFilesFromDisk(packageURL.toString(), scanFromPackage);
            }
        }

        return classFileNames;
    }

    /**
     * Поиск файлов скомпилированных классов на диске (при отладке)
     * @param rootDirectory Начиная с какой директории искать файлы
     * @param packageName Пакет, файлы которого ищутся
     * */
    public static Collection<String> getClassFilesFromDisk(String rootDirectory, String packageName) throws URISyntaxException {
        List<String> files = new ArrayList<>();

        URI uri = new URI(rootDirectory);
        File folder = new File(uri.getPath());
        File[] folderFiles = folder.listFiles();
        if (folderFiles != null) {
            String fileName;
            for (File actual : folderFiles) {
                fileName = actual.getName();
                if (actual.isDirectory()){
                    files.addAll(getClassFilesFromDisk(rootDirectory + "/" + fileName, packageName + "/" + fileName));
                } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    files.add(packageName + "/" + fileName);
                }
            }
        }
        return files;
    }

    /**
     * Поиск файлов скомпилированных классов в собранном jar файле
     * @param jarFileName Имя jar-файла, в котором производится поиск
     * @param scanFromPackage Пакет, внутри которого ищутся классы
     * */
    public static Collection<String> getClassFilesFromJar(String jarFileName, String scanFromPackage) throws IOException {
        List<String> files = new ArrayList<>();
        String entryName;
        try (JarFile jf = new JarFile(jarFileName)) {
            Enumeration<JarEntry> jarEntries = jf.entries();
            while (jarEntries.hasMoreElements()) {
                entryName = jarEntries.nextElement().getName();
                if (
                        entryName.startsWith(scanFromPackage)
                                && entryName.endsWith(".class")
                                && !entryName.contains("$")
                ) {
                    files.add(entryName);
                }
            }
        }
        return files;
    }

    /**
     * Определение класса, с которого загрузилось приложение
     * */

    public static Class<?> getMainClass() throws ClassNotFoundException {
        return Class.forName(
                Arrays.stream(new RuntimeException().getStackTrace())
                        .filter(element -> element.getMethodName().equalsIgnoreCase("main"))
                        .map(StackTraceElement::getClassName)
                        .findFirst()
                        .orElse(null)
        );
    }
}
