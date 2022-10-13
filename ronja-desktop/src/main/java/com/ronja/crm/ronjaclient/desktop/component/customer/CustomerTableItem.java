package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class CustomerTableItem {

    private final SimpleStringProperty companyName;
    private final SimpleObjectProperty<Category> category;
    private final SimpleObjectProperty<Focus> focus;
    private final SimpleObjectProperty<Status> status;
    private final Customer customer;

    public CustomerTableItem(Customer customer) {
        this.customer = Objects.requireNonNull(customer, I18N.get("customer.is.required"));

        status = new SimpleObjectProperty<>(customer.getStatus());
        companyName = new SimpleStringProperty(customer.getCompanyName());
        category = new SimpleObjectProperty<>(customer.getCategory());
        focus = new SimpleObjectProperty<>(customer.getFocus());
    }

    public String getCompanyName() {
        return companyName.get();
    }

    public ReadOnlyStringProperty companyNameProperty() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
        this.customer.setCompanyName(companyName);
    }

    public Category getCategory() {
        return category.get();
    }

    public ReadOnlyObjectProperty<Category> categoryProperty() {
        return category;
    }

    public void setCategory(Category category) {
        this.category.set(category);
        this.customer.setCategory(category);
    }

    public Focus getFocus() {
        return focus.get();
    }

    public ReadOnlyObjectProperty<Focus> focusProperty() {
        return focus;
    }

    public void setFocus(Focus focus) {
        this.focus.set(focus);
        this.customer.setFocus(focus);
    }

    public Status getStatus() {
        return status.get();
    }

    public ReadOnlyObjectProperty<Status> statusProperty() {
        return status;
    }

    public void setStatus(Status status) {
        this.status.set(status);
        this.customer.setStatus(status);
    }

    public Customer getCustomer() {
        return customer;
    }
}
