package com.ronja.crm.ronjaclient.service.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Representative {

    private int id;
    private String firstName;
    private String lastName;
    private String position;
    private String region;
    private String notice;
    private Status status;
    private LocalDate lastVisit;
    private LocalDate scheduledVisit;
    private List<String> phoneNumbers;
    private List<String> emails;
    private Customer customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDate lastVisit) {
        this.lastVisit = lastVisit;
    }

    public LocalDate getScheduledVisit() {
        return scheduledVisit;
    }

    public void setScheduledVisit(LocalDate scheduledVisit) {
        this.scheduledVisit = scheduledVisit;
    }

    public List<String> getPhoneNumbers() {
        return List.copyOf(phoneNumbers);
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = new ArrayList<>(phoneNumbers);
    }

    public List<String> getEmails() {
        return List.copyOf(emails);
    }

    public void setEmails(List<String> emails) {
        this.emails = new ArrayList<>(emails);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
