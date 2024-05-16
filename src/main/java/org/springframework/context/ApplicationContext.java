package org.springframework.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FileScanner;
import org.springframework.exceptions.*;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ApplicationContext {

    private final BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ConfigurationsException, ScheduledMethodException, IncorrectClassPropertyException, PropertyNotFoundException, PropertiesSourceException, IOException, PropertyFormatException {

        // add in nearest future
        /*
        if (!testApplicationRun(basePackage)) {
            beanFactory.instantiate(basePackage);
        }
        */

        beanFactory.populateBeans();
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
        beanFactory.startScheduleThread();
    }

    public ApplicationContext(Class<?> configuration)
            throws ReflectiveOperationException, URISyntaxException, BeanException, ScheduledMethodException, IncorrectClassPropertyException, PropertyNotFoundException, PropertiesSourceException, IOException, PropertyFormatException {
        beanFactory.instantiate(configuration);
        beanFactory.populateBeans();
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
