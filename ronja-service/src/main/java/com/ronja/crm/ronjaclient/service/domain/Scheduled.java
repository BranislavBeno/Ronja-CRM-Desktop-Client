package com.ronja.crm.ronjaclient.service.domain;

import java.time.LocalDate;

public class Scheduled {

    private String firstName;
    private String lastName;
    private LocalDate scheduledVisit;
    private String customerName;

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

    public LocalDate getScheduledVisit() {
        return scheduledVisit;
    }

    public void setScheduledVisit(LocalDate scheduledVisit) {
        this.scheduledVisit = scheduledVisit;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
