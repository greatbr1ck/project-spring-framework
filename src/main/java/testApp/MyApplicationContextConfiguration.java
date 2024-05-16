package testApp;

import org.springframework.beans.factory.annotation.Bean;
import org.springframework.beans.factory.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Configuration;
import org.springframework.beans.factory.annotation.PropertiesSource;

@Configuration
@ComponentScan(basePackage = "/home/egor/work/programming_technologies/project-6/src/main/java/testApp")
@PropertiesSource(propertiesSourcePath = "application.properties")
public class MyApplicationContextConfiguration {

    @Bean
    public PromotionsService promotionsService() {
        return new PromotionsService();
    }
}
