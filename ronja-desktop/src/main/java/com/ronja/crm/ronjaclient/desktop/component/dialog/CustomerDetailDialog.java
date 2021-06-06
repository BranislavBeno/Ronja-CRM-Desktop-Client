package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableItem;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
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

  public CustomerDetailDialog(CustomerApiClient customerApiClient,
                              CustomerTableView tableView,
                              boolean update) {
    Objects.requireNonNull(customerApiClient);
    Objects.requireNonNull(tableView);

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

    var saveButton = new Button();
    if (update) {
      customerItem = tableView.selectedCustomer().getValue();
      setUpContent(customerItem);
      setTitle("Upraviť zákazníka");
      saveButton.setText("Ulož");
      saveButton.setOnAction(e -> updateCustomer(() -> customerApiClient.updateCustomer(customerItem.getCustomer())));
    } else {
      setTitle("Pridať zákazníka");
      setUpContent();
      saveButton.setText("Pridaj");
      saveButton.setOnAction(e -> {
            var customer = new Customer();
            customer.setCompanyName(companyNameTextField.getText());
            customer.setCategory(categoryChoiceBox.getValue());
            customer.setFocus(focusChoiceBox.getValue());
            customer.setStatus(statusChoiceBox.getValue());
            customerItem = new CustomerTableItem(customer);
            tableView.addItem(customerItem);
            updateCustomer(() -> customerApiClient.createCustomer(customer));
          }
      );
    }

    var cancelButton = new Button("Zruš");
    cancelButton.setOnAction(e -> cancelOperation());

    var hBox = new HBox();
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.setSpacing(10);
    hBox.getChildren().addAll(cancelButton, saveButton);
    var detailViewPane = setUpGridPane();
    var vBox = new VBox();
    vBox.getChildren().addAll(detailViewPane, hBox);
    vBox.setPadding(new Insets(12, 10, 12, 10));
    vBox.setSpacing(10);

    var scene = new Scene(vBox, 400, 170);
    setScene(scene);
  }

  private void updateCustomer(Runnable runnable) {
    customerItem.setCompanyName(companyNameTextField.getText());
    customerItem.setCategory(categoryChoiceBox.getValue());
    customerItem.setFocus(focusChoiceBox.getValue());
    customerItem.setStatus(statusChoiceBox.getValue());
    getScene().getWindow().hide();

    Platform.runLater(runnable);
  }

  private void cancelOperation() {
    getScene().getWindow().hide();
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
    companyNameTextField.setText(customer.getCompanyName());
    categoryChoiceBox.setValue(customer.getCategory());
    focusChoiceBox.setValue(customer.getFocus());
    statusChoiceBox.setValue(customer.getStatus());
  }

  private void setUpContent() {
    companyNameTextField.setText("");
    categoryChoiceBox.setValue(Category.LEVEL_1);
    focusChoiceBox.setValue(Focus.BUILDER);
    statusChoiceBox.setValue(Status.ACTIVE);
  }
}
