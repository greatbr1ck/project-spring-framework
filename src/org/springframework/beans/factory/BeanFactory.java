package org.springframework.beans.factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.stereotype.Component;

public class BeanFactory {

  private Map<String, Object> singletons = new HashMap<>();

  public Object getBean(String beanName) {
    return singletons.get(beanName);
  }

  public void instantiate(String basePackage)
      throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    String path = basePackage.replace('.', '/');
    Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(path);
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      System.out.println(resource);
      File file = new File(resource.toURI());
      String[] fileNames = file.list();
      for (var fileName : Objects.requireNonNull(fileNames)) {
        if (fileName.endsWith(".class")) {
          String className = fileName.substring(0, fileName.lastIndexOf("."));
          Class classObject = Class.forName(basePackage + "." + className);
          if (classObject.isAnnotationPresent(Component.class)) {
            System.out.println("Component: " + classObject);
            Object instance = classObject.newInstance();
            String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
            singletons.put(beanName, instance);
          }
        }
      }
    }
  }

  public void populateProperties() throws IllegalAccessException {
    System.out.println("==populateProperties==");
    for (Object object : singletons.values()) {
      for (Field field : object.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          for (Object dependency : singletons.values()) {
            if (dependency.getClass().equals(field.getType())) {
              field.setAccessible(true);
              field.set(object, dependency);
            }
          }
        }
      }
    }
  }
  public void injectBeanNames(){
    for (String name : singletons.keySet()) {
      Object bean = singletons.get(name);
      if(bean instanceof BeanNameAware){
        ((BeanNameAware) bean).setBeanName(name);
      }
    }
  }
  public void initializeBeans(){
    for (String name : singletons.keySet()) {
      Object bean = singletons.get(name);
      for (BeanPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessBeforeInitialization(bean, name);
      }
      if (bean instanceof InitializingBean) {
        ((InitializingBean) bean).afterPropertiesSet();
      }
      for (BeanPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessAfterInitialization(bean, name);
      }
    }
  }

  private List<BeanPostProcessor> postProcessors = new ArrayList<>();
  public void addPostProcessor(BeanPostProcessor postProcessor){
    postProcessors.add(postProcessor);
  }
}
