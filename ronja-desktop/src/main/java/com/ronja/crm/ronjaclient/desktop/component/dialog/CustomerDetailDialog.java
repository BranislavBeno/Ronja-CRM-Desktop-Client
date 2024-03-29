package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableItem;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class CustomerDetailDialog extends Stage {

    private final CustomerWebClient customerWebClient;
    private final CustomerTableView customerTableView;
    private final CustomerTableItem customerItem;
    private final RepresentativeTableView representativeTableView;
    private final TextField companyNameTextField;
    private final ChoiceBox<Category> categoryChoiceBox;
    private final ChoiceBox<Focus> focusChoiceBox;
    private final ChoiceBox<Status> statusChoiceBox;
    private final Button saveButton;
    private final Button saveCloseButton;

    public CustomerDetailDialog(CustomerWebClient customerWebClient,
                                CustomerTableView customerTableView,
                                RepresentativeTableView representativeTableView,
                                boolean update) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.customerTableView = Objects.requireNonNull(customerTableView);
        this.customerItem = customerTableView.selectedCustomer().getValue();
        this.representativeTableView = Objects.requireNonNull(representativeTableView);

        initOwner(App.getMainWindow());
        initModality(Modality.WINDOW_MODAL);
        setResizable(true);

        companyNameTextField = new TextField();
        categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
        focusChoiceBox = new ChoiceBox<>();
        focusChoiceBox.setItems(FXCollections.observableArrayList(Focus.values()));
        statusChoiceBox = new ChoiceBox<>();
        statusChoiceBox.setItems(FXCollections.observableArrayList(Status.values()));
        saveButton = new Button();
        saveCloseButton = new Button();

        initialize(update);
    }

    private void initialize(boolean update) {
        if (update) {
            setUpDialogForUpdate();
        } else {
            setUpDialogForCreate();
        }

        var cancelButton = I18nUtils.menuItemForButton("label.dialog.cancel");
        cancelButton.setOnAction(e -> DesktopUtil.closeOperation(getScene()));

        var buttonBar = new HBox();
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setSpacing(10);
        buttonBar.getChildren().addAll(cancelButton, saveButton, saveCloseButton);
        GridPane detailViewPane = setUpPropertiesPane();
        VBox representativesView = setUpRepresentativesView();
        var vBox = new VBox();
        vBox.getChildren().addAll(detailViewPane, buttonBar, representativesView);
        vBox.setPadding(new Insets(12, 10, 12, 10));
        vBox.setSpacing(10);

        var scene = new Scene(vBox, 1200, 400);
        setScene(scene);
    }

    private VBox setUpRepresentativesView() {
        VBox representativesView = new VBox();
        representativesView.getChildren().addAll(new Label(getCaption("label.tab.representatives")), representativeTableView);
        representativesView.setSpacing(10);
        VBox.setVgrow(representativeTableView, Priority.ALWAYS);
        VBox.setVgrow(representativesView, Priority.ALWAYS);

        return representativesView;
    }

    private void setUpDialogForUpdate() {
        Customer customer = customerItem.getCustomer();
        setUpContent(customer);
        setTitle(I18N.get("customer.modify.title"));
        representativeTableView.setCustomer(customer);
        representativeTableView.refreshItems();
        saveButton.setText(I18N.get("label.dialog.save"));
        saveButton.setOnAction(e -> updateAction(customer, false));
        saveCloseButton.setText(I18N.get("label.dialog.save.close"));
        saveCloseButton.setOnAction(e -> updateAction(customer, true));
    }

    private void setUpDialogForCreate() {
        setUpContent();
        setTitle(I18N.get("customer.add.title"));
        representativeTableView.setCustomer(null);
        representativeTableView.refreshItems();
        saveButton.setText(I18N.get("label.dialog.add"));
        saveButton.setOnAction(e -> addAction(false));
        saveCloseButton.setText(I18N.get("label.dialog.add.close"));
        saveCloseButton.setOnAction(e -> addAction(true));
    }

    private void updateAction(Customer customer, boolean close) {
        try {
            Customer updatedCustomer = updateCustomer(customer);
            CompletableFuture<Void> cf = CompletableFuture
                    .runAsync(() -> customerWebClient.updateCustomer(updatedCustomer).block())
                    .whenComplete((r, t) -> updateCustomerItem(t));
            cf.get();
            if (close) {
                DesktopUtil.closeOperation(getScene());
            }
        } catch (Exception ex) {
            Thread.currentThread().interrupt();
            DesktopUtil.handleException(ex);
        }
    }

    private void addAction(boolean close) {
        try {
            Customer customer = provideCustomer();
            if (confirmAddAction(customer)) {
                CompletableFuture
                        .supplyAsync(() -> customerWebClient.createCustomer(customer).block())
                        .whenComplete(this::addCustomerItem)
                        .get();
            }
            if (close) {
                DesktopUtil.closeOperation(getScene());
            }
        } catch (Exception ex) {
            Thread.currentThread().interrupt();
            DesktopUtil.handleException(ex);
        }
    }

    private boolean confirmAddAction(Customer customer) throws InterruptedException, ExecutionException {
        boolean runAction = true;
        if (customerExists(customer)) {
            String message = I18N.get("customer.dialog.exists").formatted(customer.getCompanyName())
                    + System.lineSeparator()
                    + I18N.get("customer.add.message");
            runAction = Dialogs.showAlertDialog(I18N.get("customer.add.title"), message, Alert.AlertType.CONFIRMATION);
        }
        return runAction;
    }

    private boolean customerExists(Customer customer) throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> bf = CompletableFuture
                .supplyAsync(() -> DesktopUtil.fetchCustomers(customerWebClient)
                        .anyMatch(c -> c.equals(customer)));
        return bf.get();
    }

    private void addCustomerItem(Customer customer, Throwable throwable) {
        if (throwable == null) {
            CustomerTableItem item = new CustomerTableItem(customer);
            customerTableView.addItem(item);
            representativeTableView.setCustomer(customer);
            representativeTableView.refreshItems();
        }
    }

    private Customer provideCustomer() {
        var customer = new Customer();
        return updateCustomer(customer);
    }

    private void updateCustomerItem(Throwable throwable) {
        if (throwable == null) {
            customerItem.setCompanyName(companyNameTextField.getText());
            customerItem.setCategory(categoryChoiceBox.getValue());
            customerItem.setFocus(focusChoiceBox.getValue());
            customerItem.setStatus(statusChoiceBox.getValue());
            representativeTableView.refreshItems();
        }
    }

    private Customer updateCustomer(Customer customer) {
        customer.setCompanyName(companyNameTextField.getText());
        customer.setCategory(categoryChoiceBox.getValue());
        customer.setFocus(focusChoiceBox.getValue());
        customer.setStatus(statusChoiceBox.getValue());
        return customer;
    }

    private GridPane setUpPropertiesPane() {
        Label companyNameLabel = new Label(getCaption("customer.company.name"));
        Label categoryLabel = new Label(getCaption("customer.category.name"));
        Label focusLabel = new Label(getCaption("customer.focus.name"));
        Label statusLabel = new Label(getCaption("label.dialog.state"));

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

    private String getCaption(String key) {
        return I18N.get(key) + ":";
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
