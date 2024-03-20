package org.springframework.context;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.exceptions.ConfigurationsException;

public class ApplicationContext {

  private final BeanFactory beanFactory = new BeanFactory();

  public ApplicationContext(String basePackage)
          throws ReflectiveOperationException, IOException, URISyntaxException, ConfigurationsException {
    beanFactory.instantiate(basePackage);
    beanFactory.populateProperties();
  }

  public ApplicationContext(Class<?> configuration)
      throws ReflectiveOperationException, IOException, URISyntaxException {
    beanFactory.instantiate(configuration);
    beanFactory.populateProperties();
  }


  public BeanFactory getBeanFactory() {
    return beanFactory;
  }
}
