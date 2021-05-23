package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CustomerDetailView extends VBox {

  private CustomerTableItem customerItem;
  private final Label idLabel;
  private final Label companyNameLabel;
  private final Label categoryLabel;
  private final Label focusLabel;
  private final Label statusLabel;
  private final Label idLabelContent;
  private final TextField companyNameTextField;
  private final ChoiceBox<Category> categoryChoiceBox;
  private final ChoiceBox<Focus> focusChoiceBox;
  private final ChoiceBox<Status> statusChoiceBox;

  public CustomerDetailView() {
    idLabel = new Label("Id:");
    companyNameLabel = new Label("Názov spoločnosti:");
    categoryLabel = new Label("Kategória:");
    focusLabel = new Label("Zameranie:");
    statusLabel = new Label("Stav:");
    idLabelContent = new Label();
    companyNameTextField = new TextField();
    categoryChoiceBox = new ChoiceBox<>();
    categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
    focusChoiceBox = new ChoiceBox<>();
    focusChoiceBox.setItems(FXCollections.observableArrayList(Focus.values()));
    statusChoiceBox = new ChoiceBox<>();
    statusChoiceBox.setItems(FXCollections.observableArrayList(Status.values()));

    var saveButton = new Button("Ulož");
    saveButton.setOnAction(e -> updateCustomer());
    var detailViewPane = setUpGridPane();

    getChildren().addAll(detailViewPane, saveButton);
    setPadding(new Insets(12, 10, 12, 10));
    setSpacing(10);
    setAlignment(Pos.TOP_LEFT);
  }

  private void updateCustomer() {
    if (customerItem != null) {
      customerItem.setCompanyName(companyNameTextField.getText());
      customerItem.setCategory(categoryChoiceBox.getValue());
      customerItem.setFocus(focusChoiceBox.getValue());
      customerItem.setStatus(statusChoiceBox.getValue());
    }
  }

  private GridPane setUpGridPane() {
    var gridPane = new GridPane();
    gridPane.addRow(0, idLabel, idLabelContent);
    gridPane.addRow(1, companyNameLabel, companyNameTextField);
    gridPane.addRow(2, categoryLabel, categoryChoiceBox);
    gridPane.addRow(3, focusLabel, focusChoiceBox);
    gridPane.addRow(4, statusLabel, statusLabel);

    gridPane.setAlignment(Pos.CENTER_LEFT);
    gridPane.setHgap(5);
    gridPane.setVgap(5);
    var columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.ALWAYS);
    gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints);
    VBox.setVgrow(gridPane, Priority.NEVER);

    return gridPane;
  }

  public void setUpContent(CustomerTableItem customer) {
    customerItem = customer;
    idLabelContent.setText(String.valueOf(customer.getId()));
    companyNameTextField.setText(customer.getCompanyName());
    categoryChoiceBox.setValue(customer.getCategory());
    focusChoiceBox.setValue(customer.getFocus());
    statusChoiceBox.setValue(customer.getStatus());
  }
}
