package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableItem;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RonjaListView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.SaveException;
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
  private final RonjaListView phoneNumberView;
  private final RonjaListView emailView;
  private final Button saveButton;
  private final RepresentativeMapper mapper;

  public RepresentativeDetailDialog(CustomerWebClient customerWebClient,
                                    RepresentativeWebClient representativeWebClient,
                                    RepresentativeTableView tableView,
                                    RepresentativeMapper mapper,
                                    boolean update) {
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
    customerChoiceBox = new SearchableComboBox<>();
    saveButton = new Button();
    visitedDatePicker = new DatePicker();
    visitedDatePicker.setConverter(
        new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
    scheduledDatePicker = new DatePicker();
    scheduledDatePicker.setConverter(
        new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
    phoneNumberView = new RonjaListView();
    emailView = new RonjaListView();

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
    var vBox = new VBox();
    vBox.getChildren().addAll(propertiesViewPane, buttonBar);
    vBox.setPadding(new Insets(12, 10, 12, 10));
    vBox.setSpacing(10);

    var scene = new Scene(vBox, 820, 500);
    setScene(scene);
  }

  private void setUpDialogForUpdate() {
    Representative representative = representativeItem.getRepresentative();
    setUpContent(representative);
    setTitle("Upraviť reprezentanta");
    saveButton.setText("Ulož");
    saveButton.setOnAction(e -> {
      Representative updatedRepresentative = updateRepresentative(representative);
      RepresentativeDto dto = mapper.toDto(updatedRepresentative);
      try {
        CompletableFuture<Representative> cf = CompletableFuture
            .supplyAsync(() -> representativeWebClient.updateRepresentative(dto).block())
            .whenComplete(this::updateRepresentativeItem);
        cf.get();
      } catch (Exception ex) {
        Thread.currentThread().interrupt();
        String message = ex.getCause().getMessage();
        throw new SaveException(message);
      } finally {
        DesktopUtil.cancelOperation(getScene());
      }
    });
  }

  private void setUpDialogForCreate() {
    setTitle("Pridať reprezentanta");
    setUpContent();
    saveButton.setText("Pridaj");
    saveButton.setOnAction(e -> {
      Representative representative = provideRepresentative();
      RepresentativeDto dto = mapper.toDto(representative);
      try {
        CompletableFuture<Representative> cf = CompletableFuture
            .supplyAsync(() -> representativeWebClient.createRepresentative(dto).block())
            .whenComplete(this::addRepresentativeItem);
        cf.get();
      } catch (Exception ex) {
        Thread.currentThread().interrupt();
        throw new SaveException("""
            Pridanie nového reprezentanta zlyhalo.
            Preverte spojenie so serverom.""");
      } finally {
        DesktopUtil.cancelOperation(getScene());
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
      tableView.addItem(item);
      tableView.refreshItems();
    }
  }

  private void updateRepresentativeItem(Representative representative, Throwable throwable) {
    if (throwable == null) {
      representativeItem.setFirstName(representative.getFirstName());
      representativeItem.setLastName(representative.getLastName());
      representativeItem.setPosition(representative.getPosition());
      representativeItem.setRegion(representative.getRegion());
      representativeItem.setNotice(representative.getNotice());
      representativeItem.setStatus(representative.getStatus());
      representativeItem.setContactType(representative.getContactType());
      representativeItem.setLastVisit(new RonjaDate(representative.getLastVisit()));
      representativeItem.setScheduledVisit(new RonjaDate(representative.getScheduledVisit()));
      representativeItem.setPhoneNumbers(representative.getPhoneNumbers());
      representativeItem.setEmails(representative.getEmails());
      representativeItem.setCustomer(representative.getCustomer());
      tableView.refreshItems();
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
    representative.setLastVisit(visitedDatePicker.getValue());
    representative.setScheduledVisit(scheduledDatePicker.getValue());
    representative.setPhoneNumbers(phoneNumberView.getItems());
    representative.setEmails(emailView.getItems());
    representative.setCustomer(customerChoiceBox.getValue());

    return representative;
  }

  private GridPane setUpPropertiesViewPane() {
    Label firstNameLabel = new Label("Meno:");
    Label lastNameLabel = new Label("Priezvisko:");
    Label positionLabel = new Label("Pozícia:");
    Label regionLabel = new Label("Región:");
    Label noticeLabel = new Label("Poznámka:");
    Label statusLabel = new Label("Stav:");
    Label contactTypeLabel = new Label("Spôsob kontaktovania:");
    Label visitedLabel = new Label("Posledné stretnutie:");
    Label scheduledLabel = new Label("Plánované stretnutie:");
    Label customerLabel = new Label("Spoločnosť:");
    Label phonesLabel = new Label("Telefónne číslo:");
    Label emailsLabel = new Label("E-mail:");

    var gridPane = new GridPane();
    gridPane.addRow(0, firstNameLabel, firstNameTextField, positionLabel, positionTextField);
    gridPane.addRow(1, lastNameLabel, lastNameTextField, regionLabel, regionTextField);
    gridPane.addRow(2, noticeLabel, noticeTextField, customerLabel, customerChoiceBox);
    gridPane.addRow(3, statusLabel, statusChoiceBox, contactTypeLabel, contactTypeChoiceBox);
    gridPane.addRow(4, visitedLabel, visitedDatePicker, scheduledLabel, scheduledDatePicker);
    gridPane.addRow(5, phonesLabel, phoneNumberView, emailsLabel, emailView);

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

  private Optional<Customer> fetchSelectionTargetItem() {
    return Optional.ofNullable(representativeItem.getRepresentative().getCustomer());
  }

  private void setSelectionItems() {
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
    phoneNumberView.getItems().addAll(representative.getPhoneNumbers());
    emailView.getItems().addAll(representative.getEmails());
    fetchSelectionTargetItem().ifPresent(customerChoiceBox::setValue);
    setSelectionItems();
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
    setSelectionItems();
  }
}
