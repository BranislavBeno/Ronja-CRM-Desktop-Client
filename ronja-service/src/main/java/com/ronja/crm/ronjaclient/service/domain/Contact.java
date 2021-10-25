package com.ronja.crm.ronjaclient.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {

    @JsonProperty(value = "contact")
    private String content;
    private String type;
    private boolean primary;

    public Contact() {
        this("", "", false);
    }

    public Contact(String content, String type, boolean primary) {
        this.content = content;
        this.type = type;
        this.primary = primary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return content;
    }
}
