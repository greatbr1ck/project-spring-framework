package test;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

public class Main {

  public static void main(String[] args)
      throws IOException, URISyntaxException, ReflectiveOperationException {
    ApplicationContext applicationContext = new ApplicationContext("test");
    ProductService productService = (ProductService) applicationContext.getBeanFactory()
        .getBean("productService");
    System.out.println(productService.getPromotionsService());
    BeanFactory beanFactory = new BeanFactory();
    beanFactory.instantiate("test");
    beanFactory.populateProperties();
    beanFactory.injectBeanNames();
    PromotionsService promotionsService = productService.getPromotionsService();
    System.out.println("Bean name = " + promotionsService.getBeanName());
    beanFactory.initializeBeans();
  }
}
