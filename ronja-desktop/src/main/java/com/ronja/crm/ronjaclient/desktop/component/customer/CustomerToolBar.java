package com.ronja.crm.ronjaclient.desktop.component.customer;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CustomerToolBar extends GridPane {

    private final TextField searchTextField;

    public CustomerToolBar() {
        this.searchTextField = new TextField();
        HBox.setHgrow(searchTextField, Priority.ALWAYS);

        addRow(0, new Label("Hľadaj:"), searchTextField);
        setAlignment(Pos.CENTER_LEFT);
        setHgap(5);
        setPadding(new Insets(5, 5, 5, 5));
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints);
    }

    public StringProperty getSearchTextProperty() {
        return searchTextField.textProperty();
    }
}
