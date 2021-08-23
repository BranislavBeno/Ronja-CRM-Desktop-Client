package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableItem;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DialogUtil;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.Status;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;
import java.util.Objects;

public class RepresentativeDetailDialog extends Stage {

  private RepresentativeTableItem representativeItem;
  private final RepresentativeWebClient webClient;
  private final RepresentativeTableView tableView;
  private final TextField firstNameTextField;
  private final TextField lastNameTextField;
  private final TextField positionTextField;
  private final TextField regionTextField;
  private final TextField noticeTextField;
  private final DatePicker visitedDatePicker;
  private final DatePicker scheduledDatePicker;
  private final ChoiceBox<Status> statusChoiceBox;
  private final ListView<String> phoneNumbers;
  private final ListView<String> emails;
  private final Button saveButton;

  public RepresentativeDetailDialog(RepresentativeWebClient webClient,
                                    RepresentativeTableView tableView,
                                    boolean update) {
    this.webClient = Objects.requireNonNull(webClient);
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
    phoneNumbers = setUpListView();
    emails = setUpListView();

    initialize(update);
  }

  private ListView<String> setUpListView() {
    ListView<String> list = new ListView<>();
    list.setPrefHeight(55);
    list.setEditable(true);
    list.setCellFactory(TextFieldListCell.forListView());
    return list;
  }

  private void initialize(boolean update) {
    if (update) {
      setUpDialogForUpdate();
    }

    var cancelButton = new Button("Zruš");
    cancelButton.setOnAction(e -> DialogUtil.cancelOperation(getScene()));

    var buttonBar = new HBox();
    buttonBar.setAlignment(Pos.CENTER_RIGHT);
    buttonBar.setSpacing(10);
    buttonBar.getChildren().addAll(cancelButton, saveButton);
    var detailViewPane = setUpGridPane();
    var vBox = new VBox();
    vBox.getChildren().addAll(detailViewPane, buttonBar);
    vBox.setPadding(new Insets(12, 10, 12, 10));
    vBox.setSpacing(10);

    var scene = new Scene(vBox, 820, 400);
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

  private void updateRepresentative(Runnable runnable) {
    Platform.runLater(runnable);

    representativeItem.setFirstName(firstNameTextField.getText());
    representativeItem.setLastName(lastNameTextField.getText());
    representativeItem.setPosition(positionTextField.getText());
    representativeItem.setRegion(regionTextField.getText());
    representativeItem.setNotice(noticeTextField.getText());
    representativeItem.setStatus(statusChoiceBox.getValue());
    DialogUtil.cancelOperation(getScene());
  }

  private GridPane setUpGridPane() {
    Label firstNameLabel = new Label("Meno:");
    Label lastNameLabel = new Label("Priezvisko:");
    Label positionLabel = new Label("Pozícia:");
    Label regionLabel = new Label("Región:");
    Label noticeLabel = new Label("Poznámka:");
    Label visitedLabel = new Label("Posledné stretnutie:");
    Label scheduledLabel = new Label("Plánované stretnutie:");
    Label phonesLabel = new Label("Telefónne čísla:");
    Label emailsLabel = new Label("E-maily:");
    Label statusLabel = new Label("Stav:");

    var gridPane = new GridPane();
    gridPane.addRow(0, firstNameLabel, firstNameTextField, positionLabel, positionTextField);
    gridPane.addRow(1, lastNameLabel, lastNameTextField, regionLabel, regionTextField);
    gridPane.addRow(2, noticeLabel, noticeTextField, statusLabel, statusChoiceBox);
    gridPane.addRow(3, visitedLabel, visitedDatePicker, scheduledLabel, scheduledDatePicker);
    gridPane.addRow(4, phonesLabel, phoneNumbers, emailsLabel, emails);

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

  private void setUpContent(RepresentativeTableItem item) {
    firstNameTextField.setText(item.getFirstName());
    lastNameTextField.setText(item.getLastName());
    positionTextField.setText(item.getPosition());
    regionTextField.setText(item.getRegion());
    noticeTextField.setText(item.getNotice());
    statusChoiceBox.setValue(item.getStatus());
    visitedDatePicker.setValue(item.getLastVisit().date());
    scheduledDatePicker.setValue(item.getScheduledVisit().date());
    phoneNumbers.getItems().addAll(item.getPhoneNumbers());
    emails.getItems().addAll(item.getEmails());
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
