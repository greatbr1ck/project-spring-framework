package test;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.BeanFactory;

public class Main {

  public static void main(String[] args)
      throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    BeanFactory beanFactory = new BeanFactory();
    beanFactory.instantiate("test");
    beanFactory.populateProperties();
    ProductService productService = (ProductService) beanFactory.getBean("productService");
    System.out.println(productService.getPromotionsService());
  }
}
