package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableItem;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
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
import java.util.concurrent.CompletableFuture;

public final class CustomerDetailDialog extends Stage {

  private final CustomerWebClient webClient;
  private final CustomerTableView tableView;
  private final TextField companyNameTextField;
  private final ChoiceBox<Category> categoryChoiceBox;
  private final ChoiceBox<Focus> focusChoiceBox;
  private final ChoiceBox<Status> statusChoiceBox;
  private final Button saveButton;

  public CustomerDetailDialog(CustomerWebClient webClient,
                              CustomerTableView tableView,
                              boolean update) {
    this.webClient = Objects.requireNonNull(webClient);
    this.tableView = Objects.requireNonNull(tableView);

    initOwner(App.getMainWindow());
    initModality(Modality.WINDOW_MODAL);
    setResizable(false);

    companyNameTextField = new TextField();
    categoryChoiceBox = new ChoiceBox<>();
    categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
    focusChoiceBox = new ChoiceBox<>();
    focusChoiceBox.setItems(FXCollections.observableArrayList(Focus.values()));
    statusChoiceBox = new ChoiceBox<>();
    statusChoiceBox.setItems(FXCollections.observableArrayList(Status.values()));
    saveButton = new Button();

    initialize(update);
  }

  private void initialize(boolean update) {
    if (update) {
      setUpDialogForUpdate();
    } else {
      setUpDialogForCreate();
    }

    var cancelButton = new Button("Zruš");
    cancelButton.setOnAction(e -> DesktopUtil.cancelOperation(getScene()));

    var buttonBar = new HBox();
    buttonBar.setAlignment(Pos.CENTER_RIGHT);
    buttonBar.setSpacing(10);
    buttonBar.getChildren().addAll(cancelButton, saveButton);
    var detailViewPane = setUpGridPane();
    var vBox = new VBox();
    vBox.getChildren().addAll(detailViewPane, buttonBar);
    vBox.setPadding(new Insets(12, 10, 12, 10));
    vBox.setSpacing(10);

    var scene = new Scene(vBox, 400, 170);
    setScene(scene);
  }

  private void setUpDialogForUpdate() {
    Customer customer = tableView.selectedCustomer().getValue().getCustomer();
    setUpContent(customer);
    setTitle("Upraviť zákazníka");
    saveButton.setText("Ulož");
    updateCustomer(customer);
    saveButton.setOnAction(e -> {
      CompletableFuture
          .runAsync(() -> webClient.updateCustomer(customer).block())
          .whenComplete((r, t) -> updateCustomerItem(t));
      DesktopUtil.cancelOperation(getScene());
    });
  }

  private void setUpDialogForCreate() {
    setUpContent();
    setTitle("Pridať zákazníka");
    saveButton.setText("Pridaj");
    saveButton.setOnAction(e -> {
      Customer customer = provideCustomer();
      CompletableFuture
          .runAsync(() -> webClient.createCustomer(customer).block())
          .whenComplete((r, t) -> addCustomerItem(customer, t));
      DesktopUtil.cancelOperation(getScene());
    });
  }

  private void updateCustomerItem(Throwable throwable) {
    if (throwable == null) {
      CustomerTableItem item = tableView.selectedCustomer().getValue();
      item.setCompanyName(companyNameTextField.getText());
      item.setCategory(categoryChoiceBox.getValue());
      item.setFocus(focusChoiceBox.getValue());
      item.setStatus(statusChoiceBox.getValue());
    }
  }

  private void addCustomerItem(Customer customer, Throwable throwable) {
    if (throwable == null) {
      CustomerTableItem item = new CustomerTableItem(customer);
      tableView.addItem(item);
    }
  }

  private Customer provideCustomer() {
    var customer = new Customer();
    updateCustomer(customer);
    return customer;
  }

  private void updateCustomer(Customer customer) {
    customer.setCompanyName(companyNameTextField.getText());
    customer.setCategory(categoryChoiceBox.getValue());
    customer.setFocus(focusChoiceBox.getValue());
    customer.setStatus(statusChoiceBox.getValue());
  }

  private GridPane setUpGridPane() {
    Label companyNameLabel = new Label("Názov spoločnosti:");
    Label categoryLabel = new Label("Kategória:");
    Label focusLabel = new Label("Zameranie:");
    Label statusLabel = new Label("Stav:");

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

  private void setUpContent(Customer customer) {
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
