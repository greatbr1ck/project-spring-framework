package test;

import org.springframework.context.ApplicationContext;
import org.springframework.exceptions.BeanException;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args)
            throws IOException, URISyntaxException, ReflectiveOperationException, BeanException {

        ApplicationContext applicationContext = new ApplicationContext(
                MyApplicationContextConfiguration.class);
        ProductService productService = (ProductService) applicationContext.getBeanFactory()
                .getBean("test.ProductService");
        System.out.println(productService.getPromotionsService().getId());
    }
}
