package test;

import org.springframework.beans.factory.annotation.Bean;
import org.springframework.beans.factory.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Configuration;
import org.springframework.beans.factory.annotation.PropertiesSource;

@Configuration
@ComponentScan(basePackage = "test")
@PropertiesSource(propertiesSourcePath = "application.properties")
public class MyApplicationContextConfiguration {

    @Bean
    public PromotionsService promotionsService() {
        return new PromotionsService();
    }
}
