package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Delayed;
import org.springframework.beans.factory.annotation.Repeatable;
import org.springframework.beans.factory.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ProductService {

    @Autowired
    private PromotionsService promotionsService;

    public PromotionsService getPromotionsService() {
        return promotionsService;
    }

    public void setPromotionsService(PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }

    @Repeatable(value = 500, type = TimeUnit.MILLISECONDS)
    public void repeatablePromotionsServiceId() {
        System.out.println(promotionsService.getId());
    }

    @Delayed(value = 3, type = TimeUnit.SECONDS)
    public void delayedPrintHello() {
        System.out.println("Hello");
    }

}
