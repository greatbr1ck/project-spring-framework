package org.springframework.beans.factory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Bean;
import org.springframework.beans.factory.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.PreDestroy;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.exceptions.BeanException;
import org.springframework.exceptions.ConfigurationsException;

public class BeanFactory {

  private Map<String, Object> singletons = new HashMap<>();
  private Map<Class<?>, String> beanNameByType = new HashMap<>();
  private List<BeanPostProcessor> postProcessors = new ArrayList<>();


  public Object getBean(String beanName) {
    return singletons.get(beanName);
  }

  private void addBean(Object bean) throws BeanException {
    if (beanNameByType.containsKey(bean.getClass())) {
      throw new BeanException();
    } else {
      String beanClassName = bean.getClass().getName();
      String beanName = beanClassName.substring(0, 1).toLowerCase() + beanClassName.substring(1);
      singletons.put(beanName, bean);
      beanNameByType.put(bean.getClass(), beanName);
    }
  }

  public void instantiate(String basePackage)
      throws ReflectiveOperationException, URISyntaxException, BeanException, ConfigurationsException {
    try {
      Class<?> configuration = FileScanner.getConfigurations(basePackage);
      for (var method : configuration.getMethods()) {
        if (method.isAnnotationPresent(Bean.class)) {
          method.setAccessible(true);
          addBean(method.invoke(configuration.newInstance()));
        }
      }
    } catch (ClassNotFoundException ignored) {
    }
    findComponent(basePackage);
  }

  public void instantiate(Class<?> configuration)
      throws ReflectiveOperationException, URISyntaxException, BeanException {
    for (var method : configuration.getMethods()) {
      if (method.isAnnotationPresent(Bean.class)) {
        method.setAccessible(true);
        addBean(method.invoke(configuration.newInstance()));
      }
    }
    if (configuration.isAnnotationPresent(ComponentScan.class)) {
      findComponent(configuration.getAnnotation(ComponentScan.class).basePackage());
    }
  }

  public void findComponent(String basePackage)
      throws ReflectiveOperationException, URISyntaxException, BeanException {
    ArrayList<Class> componentFiles = FileScanner.getComponentFiles(basePackage);

    for (var component : Objects.requireNonNull(componentFiles)) {
      addBean(component.newInstance());
    }
  }

  public void populateProperties() throws IllegalAccessException {
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

  public void injectBeanNames() {
    for (String name : singletons.keySet()) {
      Object bean = singletons.get(name);
      if (bean instanceof BeanNameAware) {
        ((BeanNameAware) bean).setBeanName(name);
      }
    }
  }

  public void addPostProcessor(BeanPostProcessor postProcessor) {
    postProcessors.add(postProcessor);
  }

  public void initializeBeans() {
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

  public void close() {
    for (Object bean : singletons.values()) {
      for (Method method : bean.getClass().getMethods()) {
        if (method.isAnnotationPresent(PreDestroy.class)) {
          try {
            method.invoke(bean);
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
      if (bean instanceof DisposableBean) {
        ((DisposableBean) bean).destroy();
      }
    }
  }
}
