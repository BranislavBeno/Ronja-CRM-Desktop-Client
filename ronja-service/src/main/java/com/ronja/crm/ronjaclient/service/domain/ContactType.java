package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;

public enum ContactType {
  PERSONAL("contact.type.personal"),
  MAIL("contact.type.mail"),
  PHONE("contact.type.phone"),
  ON_LINE("contact.type.online");

  private final String key;

  ContactType(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return I18N.get(key);
  }
}
