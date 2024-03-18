package org.springframework.beans.factory;

import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class FileScanner {
    private String basePackage;
    private ArrayList<Class> componentFiles;

    public FileScanner(String basePackage) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.basePackage = basePackage;
        this.componentFiles = new ArrayList();
        componentFiles = new ArrayList<>();
        instantiate(basePackage);
    }
    public void instantiate(String basePackage) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String path = basePackage.replace('.', '/');
        System.out.println(path);
        URL root = ClassLoader.getSystemClassLoader().getResource(path);

        System.out.println(root);
        File file = new File(root.toURI());
        String[] childFiles = file.list();
        System.out.println("Children");
        for (var fileName : childFiles) {
            System.out.println(fileName);
        }

        System.out.println("subdirectories");
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        searchFiles(file);
        for (var cl: componentFiles) System.out.println(cl);

    }


    void searchFiles(File currentDirectory) throws ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {
        String[] childFiles = currentDirectory.list();

        for (var fileName : childFiles) {
            if (fileName.endsWith(".class")) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                Class classObject = Class.forName(basePackage + "." + className);

                if (classObject.isAnnotationPresent(Component.class)) {
                    componentFiles.add(classObject);
                }
            }

            String[] childDirectoryNames = currentDirectory.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });

            for (var childDirectoryName : childDirectoryNames) {
                String childDirectoryPath = basePackage + '/' + childDirectoryName;
                System.out.println(childDirectoryPath);
                URL childDirectoryURL = ClassLoader.getSystemClassLoader().getResource(childDirectoryPath);
                System.out.println(childDirectoryURL);
                File childDirectoryFile = new File(childDirectoryURL.toURI());
                searchFiles(childDirectoryFile);
            }
        }
    }
}
