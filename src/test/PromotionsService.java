package test;

import org.springframework.beans.factory.BeanNameAware;

public class PromotionsService implements BeanNameAware {

  private String id;
  private String beanName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  @Override
  public void setBeanName(String name) {
    beanName = name;
  }

  public String getBeanName() {
    return beanName;
  }
}
