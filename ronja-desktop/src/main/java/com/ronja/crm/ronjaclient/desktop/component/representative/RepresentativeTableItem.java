package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.service.domain.*;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.Objects;

public class RepresentativeTableItem {

  private final SimpleStringProperty firstName;
  private final SimpleStringProperty lastName;
  private final SimpleStringProperty position;
  private final SimpleStringProperty region;
  private final SimpleStringProperty notice;
  private final SimpleObjectProperty<Status> status;
  private final SimpleObjectProperty<ContactType> contactType;
  private final SimpleObjectProperty<Customer> customer;
  private final SimpleObjectProperty<RonjaDate> lastVisit;
  private final SimpleObjectProperty<RonjaDate> scheduledVisit;
  private final SimpleObjectProperty<List<Contact>> phoneNumbers;
  private final SimpleObjectProperty<List<Contact>> emails;

  private final Representative representative;

  public RepresentativeTableItem(Representative representative) {
    this.representative = Objects.requireNonNull(representative, "Je potrebné zadať reprezentanta!");

    firstName = new SimpleStringProperty(representative.getFirstName());
    lastName = new SimpleStringProperty(representative.getLastName());
    position = new SimpleStringProperty(representative.getPosition());
    region = new SimpleStringProperty(representative.getRegion());
    notice = new SimpleStringProperty(representative.getNotice());
    status = new SimpleObjectProperty<>(representative.getStatus());
    contactType = new SimpleObjectProperty<>(representative.getContactType());
    customer = new SimpleObjectProperty<>(representative.getCustomer());
    lastVisit = new SimpleObjectProperty<>(new RonjaDate(representative.getLastVisit()));
    scheduledVisit = new SimpleObjectProperty<>(new RonjaDate(representative.getScheduledVisit()));
    phoneNumbers = new SimpleObjectProperty<>(representative.getPhoneNumbers());
    emails = new SimpleObjectProperty<>(representative.getEmails());
  }

  public String getFirstName() {
    return firstName.get();
  }

  public ReadOnlyStringProperty firstNameProperty() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName.set(firstName);
    this.representative.setFirstName(firstName);
  }

  public String getLastName() {
    return lastName.get();
  }

  public ReadOnlyStringProperty lastNameProperty() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName.set(lastName);
    this.representative.setLastName(lastName);
  }

  public String getPosition() {
    return position.get();
  }

  public ReadOnlyStringProperty positionProperty() {
    return position;
  }

  public void setPosition(String position) {
    this.position.set(position);
    this.representative.setPosition(position);
  }

  public String getRegion() {
    return region.get();
  }

  public ReadOnlyStringProperty regionProperty() {
    return region;
  }

  public void setRegion(String region) {
    this.region.set(region);
    this.representative.setRegion(region);
  }

  public Customer getCustomer() {
    return customer.get();
  }

  public ReadOnlyStringProperty customerProperty() {
    String customerName = customer.get() != null ? customer.get().getCompanyName() : "";
    return new SimpleStringProperty(customerName);
  }

  public void setCustomer(Customer customer) {
    this.customer.set(customer);
    this.representative.setCustomer(customer);
  }

  public String getNotice() {
    return notice.get();
  }

  public ReadOnlyStringProperty noticeProperty() {
    return notice;
  }

  public void setNotice(String notice) {
    this.notice.set(notice);
    this.representative.setNotice(notice);
  }

  public Status getStatus() {
    return status.get();
  }

  public ReadOnlyObjectProperty<Status> statusProperty() {
    return status;
  }

  public void setStatus(Status status) {
    this.status.set(status);
    this.representative.setStatus(status);
  }

  public ContactType getContactType() {
    return contactType.get();
  }

  public ReadOnlyObjectProperty<ContactType> contactTypeProperty() {
    return contactType;
  }

  public void setContactType(ContactType contactType) {
    this.contactType.set(contactType);
    this.representative.setContactType(contactType);
  }

  public Representative getRepresentative() {
    return representative;
  }

  public RonjaDate getLastVisit() {
    return lastVisit.get();
  }

  public ReadOnlyObjectProperty<RonjaDate> lastVisitProperty() {
    return lastVisit;
  }

  public void setLastVisit(RonjaDate lastVisit) {
    this.lastVisit.set(lastVisit);
    this.representative.setLastVisit(lastVisit.date());
  }

  public RonjaDate getScheduledVisit() {
    return scheduledVisit.get();
  }

  public ReadOnlyObjectProperty<RonjaDate> scheduledVisitProperty() {
    return scheduledVisit;
  }

  public void setScheduledVisit(RonjaDate scheduledVisit) {
    this.scheduledVisit.set(scheduledVisit);
    this.representative.setScheduledVisit(scheduledVisit.date());
  }

  public List<Contact> getPhoneNumbers() {
    return phoneNumbers.get();
  }

  public ReadOnlyStringProperty phoneNumbersProperty() {
    String phoneNumber = findPrimaryOrNew(phoneNumbers);
    return new SimpleStringProperty(phoneNumber);
  }

  public void setPhoneNumbers(List<Contact> phoneNumbers) {
    this.phoneNumbers.set(phoneNumbers);
    this.representative.setPhoneNumbers(phoneNumbers);
  }

  public List<Contact> getEmails() {
    return emails.get();
  }

  public ReadOnlyStringProperty emailsProperty() {
    String email = findPrimaryOrNew(emails);
    return new SimpleStringProperty(email);
  }

  private String findPrimaryOrNew(SimpleObjectProperty<List<Contact>> contacts) {
    return contacts.get()
        .stream()
        .filter(Contact::primary)
        .findFirst()
        .orElse(contacts.get().stream().findFirst().orElse(new Contact()))
        .contact();
  }

  public void setEmails(List<Contact> emails) {
    this.emails.set(emails);
    this.representative.setEmails(emails);
  }
}
