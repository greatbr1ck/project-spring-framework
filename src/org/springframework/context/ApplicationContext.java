package org.springframework.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.exceptions.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class ApplicationContext {

    private final BeanFactory beanFactory = new BeanFactory();

    public ApplicationContext(String basePackage) throws Exception {
        beanFactory.instantiate(basePackage);
        beanFactory.populateBeans();
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
    }

    public ApplicationContext(Class<?> configuration) throws Exception {
        beanFactory.instantiate(configuration);
        beanFactory.populateBeans();
        beanFactory.populateProperties();
        beanFactory.injectBeanNames();
        beanFactory.initializeBeans();
    }


    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void close() {
        beanFactory.close();
    }
}
