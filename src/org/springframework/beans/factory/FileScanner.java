package org.springframework.beans.factory;

import org.springframework.beans.factory.annotation.Configuration;
import org.springframework.beans.factory.stereotype.Component;
import org.springframework.exceptions.ConfigurationsException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    public static ArrayList<Class> getComponentFiles(String basePackage) throws URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ArrayList<Class> componentFiles = new ArrayList<>();
        instantiate(componentFiles, basePackage);
        return componentFiles;
    }

    private static void instantiate(List<Class> componentFiles, String rootDirectoryName) throws URISyntaxException, ClassNotFoundException {
        String rootDirectoryPath = rootDirectoryName.replace('.', '/');
        URL rootDirectoryURL = ClassLoader.getSystemClassLoader().getResource(rootDirectoryPath);
        File rootDirectory = new File(rootDirectoryURL.toURI());

        searchFiles(componentFiles, rootDirectory, rootDirectoryName, Component.class);
    }

    static Class<?> getConfigurations(String rootDirectoryName) throws URISyntaxException, ClassNotFoundException, ConfigurationsException {
        String rootDirectoryPath = rootDirectoryName.replace('.', '/');
        URL rootDirectoryURL = ClassLoader.getSystemClassLoader().getResource(rootDirectoryPath);
        File rootDirectory = new File(rootDirectoryURL.toURI());
        ArrayList<Class> configurationsFiles = new ArrayList<>();

        try {
            searchFiles(configurationsFiles, rootDirectory, rootDirectoryName, Configuration.class);

            if (configurationsFiles.size() == 0) throw new ClassNotFoundException();
            if (configurationsFiles.size() > 1) throw new ConfigurationsException();

            System.out.println("conf " + configurationsFiles.get(0).toString());
            return configurationsFiles.get(0);
        } catch (ClassNotFoundException exception) {
            throw new ClassNotFoundException("Error! Project needs contain @Configuration file");
            // some code, doing without Configuration
        } catch (ConfigurationsException e) {
            throw new ConfigurationsException();
        }
    }

    private static void searchFiles(List<Class> foundFiles, File currentDirectory, String rootDirectoryName, Class annotationClass) throws ClassNotFoundException {
        File[] childFiles = currentDirectory.listFiles();

        for (var file : childFiles) {
            String path = file.getPath();
            if (path.endsWith(".class")) {
                String className = path.substring(path.indexOf(rootDirectoryName), path.lastIndexOf('.')).replace('/', '.');
                Class classObject = Class.forName(className);

                if (classObject.isAnnotationPresent(annotationClass)) {
                    foundFiles.add(classObject);
                }
            } else {
                searchFiles(foundFiles, file, rootDirectoryName, annotationClass);
            }
        }
    }
}
