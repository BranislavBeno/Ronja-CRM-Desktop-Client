package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class RepresentativeTableItem {

  private final SimpleStringProperty firstName;
  private final SimpleStringProperty lastName;
  private final SimpleStringProperty position;
  private final SimpleStringProperty region;
  private final SimpleStringProperty notice;
  private final SimpleObjectProperty<Status> status;
  private final Representative representative;

  public RepresentativeTableItem(Representative representative) {
    this.representative = Objects.requireNonNull(representative, "Je potrebné zadať reprezentanta!");

    firstName = new SimpleStringProperty(representative.getFirstName());
    lastName = new SimpleStringProperty(representative.getLastName());
    position = new SimpleStringProperty(representative.getPosition());
    region = new SimpleStringProperty(representative.getRegion());
    notice = new SimpleStringProperty(representative.getNotice());
    status = new SimpleObjectProperty<>(representative.getStatus());
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

  public Representative getRepresentative() {
    return representative;
  }
}
