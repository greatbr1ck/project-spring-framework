package testApp;

import org.springframework.beans.factory.annotation.Bean;
import org.springframework.beans.factory.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Configuration;

@Configuration
@ComponentScan(basePackage = "/home/egor/work/programming_technologies/project-6/src/main/java/testApp")
public class MyApplicationContextConfiguration {

    @Bean
    public PromotionsService promotionsService() {
        PromotionsService promotionsService = new PromotionsService();
        promotionsService.setId("189782150:13492875:1234A2875B");
        return promotionsService;
    }
}
