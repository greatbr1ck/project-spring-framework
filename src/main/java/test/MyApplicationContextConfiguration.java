package test;

import org.springframework.beans.factory.annotation.Bean;
import org.springframework.beans.factory.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Configuration;

@Configuration
@ComponentScan(basePackage = "test")
public class MyApplicationContextConfiguration {

    @Bean
    public PromotionsService promotionsService() {
        PromotionsService promotionsService = new PromotionsService();
        promotionsService.setId("189782150:13492875:1234A2875B");
        return promotionsService;
    }
}
