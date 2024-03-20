package test;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.exceptions.ConfigurationsException;

public class Main {

  public static void main(String[] args)
          throws IOException, URISyntaxException, ReflectiveOperationException, ConfigurationsException {

    ApplicationContext applicationContext = new ApplicationContext(
        MyApplicationContextConfiguration.class);
    ProductService productService = (ProductService) applicationContext.getBeanFactory()
        .getBean("test.ProductService");
    System.out.println(productService.getPromotionsService().getId());
  }
}
