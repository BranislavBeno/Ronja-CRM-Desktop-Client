package com.ronja.crm.ronjaclient.service.domain;

public enum Category {
    LEVEL_1("1"), LEVEL_2("2"), LEVEL_3("3");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
