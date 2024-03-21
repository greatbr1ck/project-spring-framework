package org.springframework.context;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.exceptions.BeanException;
import org.springframework.exceptions.ConfigurationsException;

public class ApplicationContext {

  private final BeanFactory beanFactory = new BeanFactory();

  public ApplicationContext(String basePackage)
          throws ReflectiveOperationException, URISyntaxException, BeanException, ConfigurationsException {
    beanFactory.instantiate(basePackage);
    beanFactory.populateProperties();
    beanFactory.injectBeanNames();
    beanFactory.initializeBeans();
  }

  public ApplicationContext(Class<?> configuration)
      throws ReflectiveOperationException, IOException, URISyntaxException, BeanException {
    beanFactory.instantiate(configuration);
    beanFactory.populateProperties();
    beanFactory.injectBeanNames();
    beanFactory.initializeBeans();
  }


  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public void close(){
    beanFactory.close();
  }
}
