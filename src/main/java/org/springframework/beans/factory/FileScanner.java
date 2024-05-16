package org.springframework.beans.factory;

import org.springframework.beans.factory.annotation.Configuration;
import org.springframework.beans.factory.stereotype.Component;
import org.springframework.exceptions.ConfigurationsException;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    public static ArrayList<Class<?>> getComponentFiles(String basePackageAbsolutePath) throws ClassNotFoundException {
        ArrayList<Class<?>> componentFiles = new ArrayList<>();

        // basePackageAbsolutePath example
        // basePackageAbsolutePath = "/home/egor/work/programming_technologies/project-6/src/main/java/testApp;
        instantiate(componentFiles, basePackageAbsolutePath);

        return componentFiles;
    }

    private static void instantiate(List<Class<?>> componentFiles, String rootDirectoryAbsolutePath) throws ClassNotFoundException {
        String rootDirectoryName = rootDirectoryAbsolutePath.substring(rootDirectoryAbsolutePath.lastIndexOf('/') + 1);

        searchFiles(componentFiles, rootDirectoryAbsolutePath, rootDirectoryName, Component.class);
    }

    static Class<?> getConfigurations(String rootDirectoryAbsolutePath) throws ClassNotFoundException, ConfigurationsException {
        String rootDirectoryName = rootDirectoryAbsolutePath.substring(rootDirectoryAbsolutePath.lastIndexOf('/') + 1);
        ArrayList<Class<?>> configurationsFiles = new ArrayList<>();

        try {
            searchFiles(configurationsFiles, rootDirectoryAbsolutePath, rootDirectoryName, Configuration.class);

            if (configurationsFiles.isEmpty()) throw new ClassNotFoundException();
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

    private static void searchFiles(List<Class<?>> foundFiles, String currentDirectoryAbsolutePath, String rootDirectoryName, Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        File currentDirectory = new File(currentDirectoryAbsolutePath);

        String[] childFileNames = currentDirectory.list();

        for (var childFileName : childFileNames) {
            String childFileAbsolutePath = currentDirectoryAbsolutePath + '/' + childFileName;
            File childFile = new File(childFileAbsolutePath);

            if (childFile.isDirectory()) {
                searchFiles(foundFiles, childFileAbsolutePath, rootDirectoryName, annotationClass);
            } else {
                String childClassName = childFileAbsolutePath.substring(childFileAbsolutePath.indexOf(rootDirectoryName), childFileAbsolutePath.lastIndexOf('.')).replace('/', '.');
                Class<?> childClassObject = Class.forName(childClassName);

                if (childClassObject.isAnnotationPresent(annotationClass)) {
                    foundFiles.add(childClassObject);
                }
            }
        }
    }
}
