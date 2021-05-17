package com.ronja.crm.ronjaclient.service.domain;

public class Customer {

  private int id;

  private String companyName;

  private Category category;

  private Focus focus;

  private Status status;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Focus getFocus() {
    return focus;
  }

  public void setFocus(Focus focus) {
    this.focus = focus;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
