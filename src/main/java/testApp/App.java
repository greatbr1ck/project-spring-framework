package testApp;

import org.springframework.context.ApplicationContext;
import org.springframework.exceptions.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args)
            throws IOException, URISyntaxException, ReflectiveOperationException, BeanException, InterruptedException, ScheduledMethodException, IncorrectClassPropertyException, PropertyNotFoundException, PropertiesSourceException, PropertyFormatException {
        ApplicationContext applicationContext = new ApplicationContext(
                MyApplicationContextConfiguration.class);
        ProductService productService = (ProductService) applicationContext.getBeanFactory().getBean("testApp.ProductService");
        Thread.sleep(5000);
        System.out.println("close");
        applicationContext.close();
    }
}
