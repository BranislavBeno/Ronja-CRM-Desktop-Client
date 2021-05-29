package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableItem;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class CustomerDetailDialog extends Stage {

  private CustomerTableItem customerItem;
  private final Label companyNameLabel;
  private final Label categoryLabel;
  private final Label focusLabel;
  private final Label statusLabel;
  private final TextField companyNameTextField;
  private final ChoiceBox<Category> categoryChoiceBox;
  private final ChoiceBox<Focus> focusChoiceBox;
  private final ChoiceBox<Status> statusChoiceBox;
  private final CustomerTableView customerTableView;

  public CustomerDetailDialog(CustomerTableView customerTableView) {
    this.customerTableView = Objects.requireNonNull(customerTableView);

    setTitle("Upraviť zákazníka");
    initOwner(App.getMainWindow());
    initModality(Modality.WINDOW_MODAL);
    setResizable(false);

    companyNameLabel = new Label("Názov spoločnosti:");
    categoryLabel = new Label("Kategória:");
    focusLabel = new Label("Zameranie:");
    statusLabel = new Label("Stav:");
    companyNameTextField = new TextField();
    categoryChoiceBox = new ChoiceBox<>();
    categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
    focusChoiceBox = new ChoiceBox<>();
    focusChoiceBox.setItems(FXCollections.observableArrayList(Focus.values()));
    statusChoiceBox = new ChoiceBox<>();
    statusChoiceBox.setItems(FXCollections.observableArrayList(Status.values()));
    setUpContent(this.customerTableView.selectedCustomer().getValue());

    var saveButton = new Button("Ulož");
    saveButton.setOnAction(e -> updateCustomer());
    var hBox = new HBox();
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().add(saveButton);
    var detailViewPane = setUpGridPane();
    var vBox = new VBox();
    vBox.getChildren().addAll(detailViewPane, hBox);
    vBox.setPadding(new Insets(12, 10, 12, 10));
    vBox.setSpacing(10);

    var scene = new Scene(vBox, 400, 170);
    setScene(scene);
  }

  private void updateCustomer() {
    customerItem.setCompanyName(companyNameTextField.getText());
    customerItem.setCategory(categoryChoiceBox.getValue());
    customerItem.setFocus(focusChoiceBox.getValue());
    customerItem.setStatus(statusChoiceBox.getValue());
    getScene().getWindow().hide();

    Platform.runLater(() -> customerTableView.getCustomerApiClient().updateCustomer(customerItem.getCustomer()));
  }

  private GridPane setUpGridPane() {
    var gridPane = new GridPane();
    gridPane.addRow(0, companyNameLabel, companyNameTextField);
    gridPane.addRow(1, categoryLabel, categoryChoiceBox);
    gridPane.addRow(2, focusLabel, focusChoiceBox);
    gridPane.addRow(3, statusLabel, statusChoiceBox);

    gridPane.setAlignment(Pos.CENTER_LEFT);
    gridPane.setHgap(5);
    gridPane.setVgap(5);
    var columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.ALWAYS);
    gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints);
    VBox.setVgrow(gridPane, Priority.NEVER);

    return gridPane;
  }

  private void setUpContent(CustomerTableItem customer) {
    customerItem = customer;
    companyNameTextField.setText(customer.getCompanyName());
    categoryChoiceBox.setValue(customer.getCategory());
    focusChoiceBox.setValue(customer.getFocus());
    statusChoiceBox.setValue(customer.getStatus());
  }
}
