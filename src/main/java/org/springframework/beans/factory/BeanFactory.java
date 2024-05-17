package org.springframework.beans.factory;

import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.exceptions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BeanFactory {
    private final Map<String, Object> singletons = new HashMap<>();
    private final Map<Class<?>, String> beanNameByType = new HashMap<>();
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();
    private final List<Thread> schedule = new ArrayList<>();
    private String basePackage;
    private String propertiesSourcePath = "application.properties";


    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }

    private void addBean(Object bean) throws BeanException, ScheduledMethodException {
        if (beanNameByType.containsKey(bean.getClass())) {
            throw new BeanException();
        } else {
            String beanClassName = bean.getClass().getName();
            String beanName = beanClassName.substring(0, 1).toLowerCase() + beanClassName.substring(1);
            singletons.put(beanName, bean);
            beanNameByType.put(bean.getClass(), beanName);
        }

        getScheduledMethod(bean);
    }

    private void getScheduledMethod(Object bean) throws ScheduledMethodException {
        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(Repeatable.class)) {
                if (method.getParameterCount() != 0) {
                    throw new ScheduledMethodException();
                }

                Repeatable repeatable = method.getAnnotation(Repeatable.class);

                Thread repeatableActionThread = new Thread(() -> {
                    while (true) {
                        try {
                            method.invoke(bean);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Thread.sleep(TimeUnit.MILLISECONDS.convert(repeatable.value(), repeatable.type()));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                schedule.add(repeatableActionThread);
            } else if (method.isAnnotationPresent(Delayed.class)) {
                if (method.getParameterCount() != 0) {
                    throw new ScheduledMethodException();
                }

                Delayed delayed = method.getAnnotation(Delayed.class);

                Thread repeatableActionThread = new Thread(() -> {
                    try {
                        Thread.sleep(TimeUnit.MILLISECONDS.convert(delayed.value(), delayed.type()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

                schedule.add(repeatableActionThread);
            }
        }
    }

    public void instantiate(String basePackage)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ConfigurationsException, ScheduledMethodException {
        try {
            Class<?> configuration = FileScanner.getConfigurations(basePackage);
            for (var method : configuration.getMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    method.setAccessible(true);
                    addBean(method.invoke(configuration.newInstance()));
                }
            }

            if (configuration.isAnnotationPresent(PropertiesSource.class)) {
                propertiesSourcePath = configuration.getAnnotation(PropertiesSource.class).propertiesSourcePath();
            }
        } catch (ClassNotFoundException ignored) {
        }
        findComponent(basePackage);
    }

    public void instantiate(Class<?> configuration)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ScheduledMethodException {
        for (var method : configuration.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                method.setAccessible(true);
                addBean(method.invoke(configuration.newInstance()));
            }
        }

        if (configuration.isAnnotationPresent(ComponentScan.class)) {
            basePackage = configuration.getAnnotation(ComponentScan.class).basePackage();
            findComponent(basePackage);
        }

        if (configuration.isAnnotationPresent(PropertiesSource.class)) {
            propertiesSourcePath = configuration.getAnnotation(PropertiesSource.class).propertiesSourcePath();
        }
    }

    public void findComponent(String basePackage)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ScheduledMethodException {
        ArrayList<Class<?>> componentFiles = FileScanner.getComponentFiles(basePackage);

        for (var component : Objects.requireNonNull(componentFiles)) {
            addBean(component.newInstance());
        }
    }

    public void populateBeans() throws IllegalAccessException {
        for (Object object : singletons.values()) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object dependency = singletons.get(beanNameByType.get(field.getType()));
                    field.setAccessible(true);
                    field.set(object, dependency);
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
        singletons.forEach((name, bean) ->{
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessBeforeInitialization(bean, name);
            }
            if (bean instanceof org.springframework.beans.factory.InitializingBean) {
                ((org.springframework.beans.factory.InitializingBean) bean).afterPropertiesSet();
            }
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessAfterInitialization(bean, name);
            }
        });
    }


    public void startScheduleThread() {
        for (Thread thread : schedule) {
            thread.start();
        }
    }

    public void populateProperties() throws PropertiesSourceException, IOException, PropertyFormatException, PropertyNotFoundException, IllegalAccessException, IncorrectClassPropertyException {
        for (Object object : singletons.values()) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Value.class)) {
                    String stringProperty = field.getAnnotation(Value.class).property();

                    if (stringProperty != null && stringProperty.matches("\\$\\{.+\\}")) {
                        Path propertiesSourceFile = Path.of("src/resources/" + propertiesSourcePath);
                        System.out.println(propertiesSourceFile);

                        if (!Files.exists(propertiesSourceFile)) {
                            throw new PropertiesSourceException();
                        }

                        String nameProperty = stringProperty.substring(2, stringProperty.length() - 1);

                        boolean find = false;

                        List<String> propertiesLines = Files.readAllLines(propertiesSourceFile);
                        for (String line : propertiesLines) {
                            if(line.isEmpty() || line.charAt(0) == '#') {
                                continue;
                            }

                            String[] splitLine = line.split("=");

                            if (splitLine.length != 2 || splitLine[0] == null || splitLine[1] == null) {
                                throw new PropertyFormatException();
                            }

                            if (nameProperty.equals(splitLine[0])) {
                                stringProperty = splitLine[1];
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            throw new PropertyNotFoundException();
                        }
                    }

                    field.setAccessible(true);
                    if (field.getType().isAssignableFrom(String.class)) {
                        field.set(object, stringProperty);
                    } else if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)) {
                        field.set(object, Integer.parseInt(stringProperty));
                    } else if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class)) {
                        field.set(object, Long.parseLong(stringProperty));
                    } else if (field.getType().isAssignableFrom(Float.class) || field.getType().isAssignableFrom(float.class)) {
                        field.set(object, Float.parseFloat(stringProperty));
                    } else if (field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(double.class)) {
                        field.set(object, Double.parseDouble(stringProperty));
                    } else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class)) {
                        field.set(object, Boolean.parseBoolean(stringProperty));
                    } else {
                        throw new IncorrectClassPropertyException();
                    }

                }
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

        for (Thread thread: schedule) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.interrupt();
        }

        //schedule.forEach(Thread::stop);
    }
}

