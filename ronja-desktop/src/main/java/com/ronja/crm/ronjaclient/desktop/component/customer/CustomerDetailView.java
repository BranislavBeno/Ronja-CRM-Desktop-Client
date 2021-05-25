package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CustomerDetailView extends VBox {

  private CustomerTableItem customerItem;
  private final CustomerTableView customerTableView;
  private final Label companyNameLabel;
  private final Label categoryLabel;
  private final Label focusLabel;
  private final Label statusLabel;
  private final TextField companyNameTextField;
  private final ChoiceBox<Category> categoryChoiceBox;
  private final ChoiceBox<Focus> focusChoiceBox;
  private final ChoiceBox<Status> statusChoiceBox;
  private final CustomerApiClient customerApiClient;

  public CustomerDetailView(CustomerApiClient customerApiClient, CustomerTableView customerTableView) {
    this.customerApiClient = Objects.requireNonNull(customerApiClient);
    this.customerTableView = Objects.requireNonNull(customerTableView);

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

    var saveButton = new Button("Ulož");
    saveButton.setOnAction(e -> updateCustomer());
    var deleteButton = new Button("Odstráň");
    deleteButton.setOnAction(e -> deleteCustomer());
    var buttonBar = new HBox();
    buttonBar.getChildren().addAll(saveButton, deleteButton);
    buttonBar.setSpacing(10);
    HBox.setHgrow(buttonBar, Priority.ALWAYS);

    var detailViewPane = setUpGridPane();
    getChildren().addAll(detailViewPane, buttonBar);
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

      Platform.runLater(() -> customerApiClient.updateCustomer(customerItem.getCustomer()));
    }
  }

  private void deleteCustomer() {
    if (customerItem != null) {
      var title = "Zmazanie zákazníka";
      var message = String.format("Skutočne chcete zmazať zákazníka '%s'?",
          customerItem.companyNameProperty().get());
      if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
        CompletableFuture
            .runAsync(() -> customerApiClient.deleteCustomer(customerItem.getCustomer().getId()))
            .whenComplete((r, t) -> {
              if (t == null) {
                Platform.runLater(() -> customerTableView.getTableItems().remove(customerItem));
              } else {
                customerTableView.refreshItems();
              }
            });
      }
    }

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

  public void setUpContent(CustomerTableItem customer) {
    customerItem = customer;
    companyNameTextField.setText(customer.getCompanyName());
    categoryChoiceBox.setValue(customer.getCategory());
    focusChoiceBox.setValue(customer.getFocus());
    statusChoiceBox.setValue(customer.getStatus());
  }
}
