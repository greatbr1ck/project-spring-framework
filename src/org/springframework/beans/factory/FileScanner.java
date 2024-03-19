package org.springframework.beans.factory;

import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FileScanner {
    private static ArrayList<Class> componentFiles = new ArrayList<>();

    public static void instantiate(String rootDirectoryName) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String rootDirectoryPath = rootDirectoryName.replace('.', '/');
        URL rootDirectoryURL = ClassLoader.getSystemClassLoader().getResource(rootDirectoryPath);
        File rootDirectory = new File(rootDirectoryURL.toURI());

        searchFiles(rootDirectory, rootDirectoryName);
        System.out.println("found componentFiles");
        for (var clazz: componentFiles) System.out.println(clazz.toString());
    }

    static void searchFiles(File currentDirectory, String rootDirectoryName) throws ClassNotFoundException {
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

    static ArrayList<Class> getComponentFiles() {
        return componentFiles;
    }
}
