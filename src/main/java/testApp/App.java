package testApp;

import org.springframework.context.ApplicationContext;
import org.springframework.exceptions.BeanException;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args)
            throws URISyntaxException, ReflectiveOperationException, BeanException, IOException {

        ApplicationContext applicationContext = new ApplicationContext(
                MyApplicationContextConfiguration.class);
        ProductService productService = (ProductService) applicationContext.getBeanFactory()
                .getBean("testApp.ProductService");
        System.out.println(productService.getPromotionsService().getId());
    }
}
