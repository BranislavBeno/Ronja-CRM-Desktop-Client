package com.ronja.crm.ronjaclient.service.domain;

public record Contact(String contact, String type, boolean primary) {

  public Contact() {
    this("", "", false);
  }

  @Override
  public String toString() {
    return contact;
  }
}
