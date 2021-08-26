package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableItem;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RonjaListView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.Status;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import org.controlsfx.control.ListSelectionView;

import java.time.LocalDate;
import java.util.Objects;

public class RepresentativeDetailDialog extends Stage {

    private RepresentativeTableItem representativeItem;
    private final CustomerWebClient customerWebClient;
    private final RepresentativeWebClient representativeWebClient;
    private final RepresentativeTableView tableView;
    private final TextField firstNameTextField;
    private final TextField lastNameTextField;
    private final TextField positionTextField;
    private final TextField regionTextField;
    private final TextField noticeTextField;
    private final DatePicker visitedDatePicker;
    private final DatePicker scheduledDatePicker;
    private final ChoiceBox<Status> statusChoiceBox;
    private final RonjaListView phoneNumberView;
    private final RonjaListView emailView;
    private final ListSelectionView<Customer> customerSelectionView;
    private final Button saveButton;

    public RepresentativeDetailDialog(CustomerWebClient customerWebClient,
                                      RepresentativeWebClient representativeWebClient,
                                      RepresentativeTableView tableView,
                                      boolean update) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeWebClient = Objects.requireNonNull(representativeWebClient);
        this.tableView = Objects.requireNonNull(tableView);

        initOwner(App.getMainWindow());
        initModality(Modality.WINDOW_MODAL);
        setResizable(false);

        firstNameTextField = new TextField();
        lastNameTextField = new TextField();
        positionTextField = new TextField();
        regionTextField = new TextField();
        noticeTextField = new TextField();
        statusChoiceBox = new ChoiceBox<>();
        statusChoiceBox.setItems(FXCollections.observableArrayList(Status.values()));
        saveButton = new Button();
        visitedDatePicker = new DatePicker();
        visitedDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
        scheduledDatePicker = new DatePicker();
        scheduledDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
        phoneNumberView = new RonjaListView();
        emailView = new RonjaListView();
        customerSelectionView = new ListSelectionView<>();

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
        var propertiesViewPane = setUpPropertiesViewPane();
        var customerSelectionPane = setUpCustomerSelectionPane();
        var vBox = new VBox();
        vBox.getChildren().addAll(propertiesViewPane, customerSelectionPane, buttonBar);
        vBox.setPadding(new Insets(12, 10, 12, 10));
        vBox.setSpacing(10);

        var scene = new Scene(vBox, 820, 500);
        setScene(scene);
    }

    private void setUpDialogForUpdate() {
        representativeItem = tableView.selectedRepresentative().getValue();
        setUpContent(representativeItem);
        setTitle("Upraviť reprezentanta");
        saveButton.setText("Ulož");
        saveButton.setOnAction(e -> updateRepresentative(() -> {
            Representative representative = representativeItem.getRepresentative();
//      webClient.updateCustomer(customer).block();
        }));
    }

    private void setUpDialogForCreate() {
        setTitle("Pridať reprezentanta");
        setUpContent();
        saveButton.setText("Pridaj");
        saveButton.setOnAction(e -> {
                    var representative = new Representative();
                    representative.setFirstName(firstNameTextField.getText());
                    representative.setLastName(lastNameTextField.getText());
                    representative.setPosition(positionTextField.getText());
                    representative.setRegion(regionTextField.getText());
                    representative.setNotice(noticeTextField.getText());
                    representative.setStatus(statusChoiceBox.getValue());
                    representative.setLastVisit(visitedDatePicker.getValue());
                    representative.setScheduledVisit(scheduledDatePicker.getValue());
                    representative.setPhoneNumbers(phoneNumberView.getItems());
                    representative.setEmails(emailView.getItems());
                    representativeItem = new RepresentativeTableItem(representative);
                    tableView.addItem(representativeItem);
//          updateRepresentative(() -> webClient.createRepresentative(representative).block());
                }
        );
    }

    private void updateRepresentative(Runnable runnable) {
        Platform.runLater(runnable);

        representativeItem.setFirstName(firstNameTextField.getText());
        representativeItem.setLastName(lastNameTextField.getText());
        representativeItem.setPosition(positionTextField.getText());
        representativeItem.setRegion(regionTextField.getText());
        representativeItem.setNotice(noticeTextField.getText());
        representativeItem.setStatus(statusChoiceBox.getValue());
        DesktopUtil.cancelOperation(getScene());
    }

    private GridPane setUpPropertiesViewPane() {
        Label firstNameLabel = new Label("Meno:");
        Label lastNameLabel = new Label("Priezvisko:");
        Label positionLabel = new Label("Pozícia:");
        Label regionLabel = new Label("Región:");
        Label noticeLabel = new Label("Poznámka:");
        Label statusLabel = new Label("Stav:");
        Label visitedLabel = new Label("Posledné stretnutie:");
        Label scheduledLabel = new Label("Plánované stretnutie:");
        Label phonesLabel = new Label("Telefónne číslo:");
        Label emailsLabel = new Label("E-mail:");

        var gridPane = new GridPane();
        gridPane.addRow(0, firstNameLabel, firstNameTextField, positionLabel, positionTextField);
        gridPane.addRow(1, lastNameLabel, lastNameTextField, regionLabel, regionTextField);
        gridPane.addRow(2, noticeLabel, noticeTextField, statusLabel, statusChoiceBox);
        gridPane.addRow(3, visitedLabel, visitedDatePicker, scheduledLabel, scheduledDatePicker);
        gridPane.addRow(4, phonesLabel, phoneNumberView, emailsLabel, emailView);

        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(), columnConstraints, new ColumnConstraints(), columnConstraints);
        VBox.setVgrow(gridPane, Priority.NEVER);

        return gridPane;
    }

    private GridPane setUpCustomerSelectionPane() {
        Label customerLabel = new Label("Spoločnosť:");

        var gridPane = new GridPane();
        gridPane.addRow(0, customerLabel, customerSelectionView);

        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints);
        VBox.setVgrow(gridPane, Priority.NEVER);

        customerSelectionView.setSourceHeader(new Label("K dispozícii:"));
        customerSelectionView.setTargetHeader(new Label("Vybraná:"));
        Customer customer = addSelectionTargetItem();
        addSelectionSourceItems(customer);

        return gridPane;
    }

    private Customer addSelectionTargetItem() {
        if (representativeItem != null) {
            Customer customer = representativeItem.getRepresentative().getCustomer();
            if (customer != null) {
                customerSelectionView.getTargetItems().add(customer);
            }
            return customer;
        }
        return null;
    }

    private void addSelectionSourceItems(Customer customer) {
        Platform.runLater(() -> DesktopUtil.fetchCustomers(customerWebClient)
                .filter(c -> !c.equals(customer))
                .forEach(this::addItem));
    }

    private void addItem(Customer customer) {
        customerSelectionView.getSourceItems().add(customer);
    }

    private void setUpContent(RepresentativeTableItem item) {
        firstNameTextField.setText(item.getFirstName());
        lastNameTextField.setText(item.getLastName());
        positionTextField.setText(item.getPosition());
        regionTextField.setText(item.getRegion());
        noticeTextField.setText(item.getNotice());
        statusChoiceBox.setValue(item.getStatus());
        visitedDatePicker.setValue(item.getLastVisit().date());
        scheduledDatePicker.setValue(item.getScheduledVisit().date());
        phoneNumberView.getItems().addAll(item.getPhoneNumbers());
        emailView.getItems().addAll(item.getEmails());
    }

    private void setUpContent() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        positionTextField.setText("");
        regionTextField.setText("");
        noticeTextField.setText("");
        statusChoiceBox.setValue(Status.ACTIVE);
        visitedDatePicker.setValue(LocalDate.now());
        scheduledDatePicker.setValue(LocalDate.now());
    }
}