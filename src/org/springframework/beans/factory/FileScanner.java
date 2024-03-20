package org.springframework.beans.factory;

import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import test.MyApplicationContextConfiguration;

public class FileScanner {
    private static ArrayList<Class> componentFiles = new ArrayList<>();

    public static ArrayList<Class> getComponentFiles(String basePackage) throws URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        instantiate(basePackage);
        return componentFiles;
    }

    private static void instantiate(String rootDirectoryName) throws URISyntaxException, ClassNotFoundException {
        String rootDirectoryPath = rootDirectoryName.replace('.', '/');
        URL rootDirectoryURL = ClassLoader.getSystemClassLoader().getResource(rootDirectoryPath);
        File rootDirectory = new File(rootDirectoryURL.toURI());
        searchFiles(rootDirectory, rootDirectoryName);
    }

    private static void searchFiles(File currentDirectory, String rootDirectoryName) throws ClassNotFoundException {
        File[] childFiles = currentDirectory.listFiles();

        for (var file : childFiles) {
            String path = file.getPath();
            if (path.endsWith(".class")) {
                String className = path.substring(path.indexOf(rootDirectoryName), path.lastIndexOf('.')).replace('/', '.');
                Class classObject = Class.forName(className);

                if (classObject.isAnnotationPresent(Component.class)) {
                    componentFiles.add(classObject);
                }
            } else {
                searchFiles(file, rootDirectoryName);
            }
        }
    }

    static Class<?> getConfigurations() {
        // TODO write method
        // Метод должен найти и вернуть файл помеченный @Configuration, или вернуть null если такого нет
        return null;
    }
}
