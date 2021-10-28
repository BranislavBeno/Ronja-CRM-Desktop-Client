package com.ronja.crm.ronjaclient.service.domain;

import java.util.Objects;

public class Customer {

  private int id = 0;

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

  public boolean isSame(Customer customer) {
    return Objects.equals(companyName, customer.companyName)
        && Objects.equals(category, customer.category)
        && Objects.equals(focus, customer.focus)
        && Objects.equals(status, customer.status);
  }

  @Override
  public String toString() {
    return companyName != null ? companyName : "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return Objects.equals(companyName, customer.companyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, companyName);
  }
}
