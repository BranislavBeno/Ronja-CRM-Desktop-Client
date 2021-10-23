package com.ronja.crm.ronjaclient.service.domain;

public enum ContactType {
  PERSONAL("Osobne"),
  MAIL("Mailom"),
  PHONE("Telefónom"),
  ON_LINE("On-line");

  private final String label;

  ContactType(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }
}
