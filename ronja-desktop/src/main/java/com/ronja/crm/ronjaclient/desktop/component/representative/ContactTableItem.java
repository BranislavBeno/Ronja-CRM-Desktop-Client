package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.service.domain.Contact;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class ContactTableItem {

    private final SimpleStringProperty content;
    private final SimpleStringProperty type;
    private final SimpleBooleanProperty primary;
    private final Contact contact;

    public ContactTableItem(Contact contact) {
        this.contact = Objects.requireNonNull(contact);

        this.content = new SimpleStringProperty(contact.getContent());
        this.type = new SimpleStringProperty(contact.getType());
        this.primary = new SimpleBooleanProperty(contact.isPrimary());
    }

    public String getContent() {
        return content.get();
    }

    public ReadOnlyStringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
        this.contact.setContent(content);
    }

    public String getType() {
        return type.get();
    }

    public ReadOnlyStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
        this.contact.setType(type);
    }

    public boolean isPrimary() {
        return primary.get();
    }

    public ReadOnlyStringProperty primaryProperty() {
        return new SimpleStringProperty(primary.get() ? "*" : "");
    }

    public void setPrimary(boolean primary) {
        this.primary.set(primary);
        this.contact.setPrimary(primary);
    }

    public Contact getContact() {
        return contact;
    }
}
