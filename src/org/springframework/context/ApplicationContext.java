package org.springframework.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.exceptions.BeanException;
import org.springframework.exceptions.ConfigurationsException;
import org.springframework.exceptions.ScheduledMethodException;

import java.net.URISyntaxException;

public class ApplicationContext {

    private final BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ConfigurationsException, ScheduledMethodException {
        beanFactory.instantiate(basePackage);
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
        beanFactory.startScheduleThread();
    }

    public ApplicationContext(Class<?> configuration)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ScheduledMethodException {
        beanFactory.instantiate(configuration);
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
        beanFactory.startScheduleThread();
    }


    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void close() {
        beanFactory.close();
    }
}
