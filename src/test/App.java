package test;

import org.springframework.context.ApplicationContext;
import org.springframework.exceptions.BeanException;
import org.springframework.exceptions.ScheduledMethodException;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args)
            throws IOException, URISyntaxException, ReflectiveOperationException, BeanException, InterruptedException, ScheduledMethodException {
        ApplicationContext applicationContext = new ApplicationContext(
                MyApplicationContextConfiguration.class);
        ProductService productService = (ProductService) applicationContext.getBeanFactory().getBean("test.ProductService");
        Thread.sleep(5000);
        System.out.println("close");
        applicationContext.close();
    }
}
