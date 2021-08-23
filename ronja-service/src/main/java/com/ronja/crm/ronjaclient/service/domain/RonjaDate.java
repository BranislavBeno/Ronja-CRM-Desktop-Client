package com.ronja.crm.ronjaclient.service.domain;

import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;

import java.time.LocalDate;

public record RonjaDate(LocalDate date) {

  @Override
  public String toString() {
    return date.format(DateTimeUtil.DATE_TIME_FORMATTER);
  }
}
