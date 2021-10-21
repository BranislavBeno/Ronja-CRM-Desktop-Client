package com.ronja.crm.ronjaclient.service.dto;

import com.ronja.crm.ronjaclient.service.domain.Contact;
import com.ronja.crm.ronjaclient.service.domain.Representative;

import java.time.LocalDate;
import java.util.List;

public class RepresentativeMapper {

  public RepresentativeDto toDto(Representative representative) {
    int id = representative.getId();
    String firstName = representative.getFirstName();
    String lastName = representative.getLastName();
    String region = representative.getRegion();
    String notice = representative.getNotice();
    String position = representative.getPosition();
    String status = representative.getStatus().name();
    LocalDate lastVisit = representative.getLastVisit();
    LocalDate scheduledVisit = representative.getScheduledVisit();
    List<Contact> phoneNumbers = representative.getPhoneNumbers();
    List<Contact> emails = representative.getEmails();
    int customerId = representative.getCustomer() != null
        ? representative.getCustomer().getId()
        : 0;
    String contactType = representative.getContactType();

    return new RepresentativeDto(id, firstName, lastName, position, region, notice, status, lastVisit, scheduledVisit,
        phoneNumbers, emails, customerId, contactType);
  }
}
