package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.desktop.component.representative.ContactTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableItem;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.*;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeDto;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
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
import org.controlsfx.control.SearchableComboBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RepresentativeDetailDialog extends Stage {

    private final CustomerWebClient customerWebClient;
    private final RepresentativeWebClient representativeWebClient;
    private final RepresentativeTableView tableView;
    private final RepresentativeTableItem representativeItem;
    private final RepresentativeMapper mapper;
    private final TextField firstNameTextField;
    private final TextField lastNameTextField;
    private final TextField positionTextField;
    private final TextField regionTextField;
    private final TextField noticeTextField;
    private final DatePicker visitedDatePicker;
    private final DatePicker scheduledDatePicker;
    private final ChoiceBox<Status> statusChoiceBox;
    private final ChoiceBox<ContactType> contactTypeChoiceBox;
    private final SearchableComboBox<Customer> customerChoiceBox;
    private final ContactTableView phoneTableView;
    private final ContactTableView emailTableView;
    private final Button saveButton;

    public RepresentativeDetailDialog(CustomerWebClient customerWebClient,
                                      RepresentativeWebClient representativeWebClient,
                                      RepresentativeTableView tableView,
                                      RepresentativeMapper mapper,
                                      boolean update,
                                      boolean forDialog) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeWebClient = Objects.requireNonNull(representativeWebClient);
        this.tableView = Objects.requireNonNull(tableView);
        this.representativeItem = tableView.selectedRepresentative().getValue();
        this.mapper = mapper;

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
        contactTypeChoiceBox = new ChoiceBox<>();
        contactTypeChoiceBox.setItems(FXCollections.observableArrayList(ContactType.values()));
        saveButton = new Button();
        visitedDatePicker = new DatePicker();
        visitedDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
        scheduledDatePicker = new DatePicker();
        scheduledDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
        phoneTableView = new ContactTableView();
        emailTableView = new ContactTableView();
        customerChoiceBox = new SearchableComboBox<>();
        customerChoiceBox.setValue(tableView.getCustomer());
        if (forDialog) {
            customerChoiceBox.setDisable(true);
        }

        initialize(update);
    }

    private void initialize(boolean update) {
        if (update) {
            setUpDialogForUpdate();
        } else {
            setUpDialogForCreate();
        }

        var cancelButton = I18nUtils.menuItemForButton("label.dialog.cancel");
        cancelButton.setOnAction(_ -> DesktopUtil.closeOperation(getScene()));

        var buttonBar = new HBox();
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setSpacing(10);
        buttonBar.getChildren().addAll(cancelButton, saveButton);
        GridPane propertiesViewPane = setUpPropertiesViewPane();
        var vBox = new VBox();
        vBox.getChildren().addAll(propertiesViewPane, buttonBar);
        vBox.setPadding(new Insets(12, 10, 12, 10));
        vBox.setSpacing(10);

        var scene = new Scene(vBox, 820, 310);
        setScene(scene);
    }

    private void setUpDialogForUpdate() {
        Representative representative = representativeItem.getRepresentative();
        setUpContent(representative);
        setTitle(I18N.get("representative.modify.title"));
        saveButton.setText(I18N.get("label.dialog.save"));
        saveButton.setOnAction(_ -> {
            Representative updatedRepresentative = updateRepresentative(representative);
            RepresentativeDto dto = mapper.toDto(updatedRepresentative);
            try {
                CompletableFuture<Void> cf = CompletableFuture
                        .runAsync(() -> representativeWebClient.updateRepresentative(dto).block())
                        .whenComplete((_, t) -> updateRepresentativeItem(t));
                cf.get();
                DesktopUtil.closeOperation(getScene());
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
                DesktopUtil.handleException(ex);
            }
        });
    }

    private void setUpDialogForCreate() {
        setTitle(I18N.get("representative.add.title"));
        setUpContent();
        saveButton.setText(I18N.get("label.dialog.add"));
        saveButton.setOnAction(_ -> {
            Representative representative = provideRepresentative();
            RepresentativeDto dto = mapper.toDto(representative);
            try {
                CompletableFuture<Representative> cf = CompletableFuture
                        .supplyAsync(() -> representativeWebClient.createRepresentative(dto).block())
                        .whenComplete(this::addRepresentativeItem);
                cf.get();
                DesktopUtil.closeOperation(getScene());
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
                DesktopUtil.handleException(ex);
            }
        });
    }

    private Representative provideRepresentative() {
        var representative = new Representative();
        return updateRepresentative(representative);
    }

    private void addRepresentativeItem(Representative representative, Throwable throwable) {
        if (throwable == null) {
            RepresentativeTableItem item = new RepresentativeTableItem(representative);
            item.setCustomer(customerChoiceBox.getValue());
            tableView.addItem(item);
        }
    }

    private void updateRepresentativeItem(Throwable throwable) {
        if (throwable == null) {
            representativeItem.setFirstName(firstNameTextField.getText());
            representativeItem.setLastName(lastNameTextField.getText());
            representativeItem.setPosition(positionTextField.getText());
            representativeItem.setRegion(regionTextField.getText());
            representativeItem.setNotice(noticeTextField.getText());
            representativeItem.setStatus(statusChoiceBox.getValue());
            representativeItem.setContactType(contactTypeChoiceBox.getValue());
            representativeItem.setLastVisit(new RonjaDate(visitedDatePicker.getEditor().getText()));
            representativeItem.setScheduledVisit(new RonjaDate(scheduledDatePicker.getEditor().getText()));
            representativeItem.setPhoneNumbers(phoneTableView.getContacts());
            representativeItem.setEmails(emailTableView.getContacts());
            representativeItem.setCustomer(customerChoiceBox.getValue());
        }
    }

    private Representative updateRepresentative(Representative representative) {
        representative.setFirstName(firstNameTextField.getText());
        representative.setLastName(lastNameTextField.getText());
        representative.setPosition(positionTextField.getText());
        representative.setRegion(regionTextField.getText());
        representative.setNotice(noticeTextField.getText());
        representative.setStatus(statusChoiceBox.getValue());
        representative.setContactType(contactTypeChoiceBox.getValue());
        representative.setLastVisit(DateTimeUtil.parse(visitedDatePicker.getEditor().getText()));
        representative.setScheduledVisit(DateTimeUtil.parse(scheduledDatePicker.getEditor().getText()));
        representative.setPhoneNumbers(phoneTableView.getContacts());
        representative.setEmails(emailTableView.getContacts());
        representative.setCustomer(customerChoiceBox.getValue());

        return representative;
    }

    private GridPane setUpPropertiesViewPane() {
        Label firstNameLabel = new Label(getCaption("representative.first.name"));
        Label lastNameLabel = new Label(getCaption("representative.sure.name"));
        Label positionLabel = new Label(getCaption("representative.position.name"));
        Label regionLabel = new Label(getCaption("representative.region.name"));
        Label noticeLabel = new Label(getCaption("representative.note.name"));
        Label statusLabel = new Label(getCaption("label.dialog.state"));
        Label contactTypeLabel = new Label(getCaption("representative.contacted.by"));
        Label visitedLabel = new Label(getCaption("representative.last.visit"));
        Label scheduledLabel = new Label(getCaption("representative.scheduled.visit"));
        Label customerLabel = new Label(getCaption("representative.company.name"));
        Label phonesLabel = new Label(getCaption("representative.phone.name"));
        Label emailsLabel = new Label(getCaption("representative.email.name"));

        var gridPane = new GridPane();
        gridPane.addRow(0, firstNameLabel, firstNameTextField, positionLabel, positionTextField);
        gridPane.addRow(1, lastNameLabel, lastNameTextField, regionLabel, regionTextField);
        gridPane.addRow(2, noticeLabel, noticeTextField, customerLabel, customerChoiceBox);
        gridPane.addRow(3, statusLabel, statusChoiceBox, contactTypeLabel, contactTypeChoiceBox);
        gridPane.addRow(4, visitedLabel, visitedDatePicker, scheduledLabel, scheduledDatePicker);
        gridPane.addRow(5, phonesLabel, phoneTableView, emailsLabel, emailTableView);

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

    private String getCaption(String key) {
        return I18N.get(key) + ":";
    }

    private Optional<Customer> fetchCustomer() {
        return Optional.ofNullable(representativeItem.getRepresentative().getCustomer());
    }

    private void fetchCustomerList() {
        Platform.runLater(() -> {
            List<Customer> source = DesktopUtil.fetchCustomers(customerWebClient).toList();
            customerChoiceBox.setItems(FXCollections.observableArrayList(source));
        });
    }

    private void setUpContent(Representative representative) {
        firstNameTextField.setText(representative.getFirstName());
        lastNameTextField.setText(representative.getLastName());
        positionTextField.setText(representative.getPosition());
        regionTextField.setText(representative.getRegion());
        noticeTextField.setText(representative.getNotice());
        statusChoiceBox.setValue(representative.getStatus());
        contactTypeChoiceBox.setValue(representative.getContactType());
        visitedDatePicker.setValue(representative.getLastVisit());
        scheduledDatePicker.setValue(representative.getScheduledVisit());
        phoneTableView.addContacts(representative.getPhoneNumbers());
        emailTableView.addContacts(representative.getEmails());
        fetchCustomer().ifPresent(customerChoiceBox::setValue);
        fetchCustomerList();
    }

    private void setUpContent() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        positionTextField.setText("");
        regionTextField.setText("");
        noticeTextField.setText("");
        statusChoiceBox.setValue(Status.ACTIVE);
        contactTypeChoiceBox.setValue(ContactType.PERSONAL);
        visitedDatePicker.setValue(LocalDate.now());
        scheduledDatePicker.setValue(LocalDate.now());
        fetchCustomerList();
    }
}
