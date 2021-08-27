package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.RonjaDate;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RepresentativeTableView extends VBox {

  @Value("${client.customers.base-url}")
  String customerBaseUrl;
  @Value("${client.representatives.base-url}")
  String representativeBaseUrl;

  @Autowired
  private final CustomerWebClient customerWebClient;
  @Autowired
  private final RepresentativeWebClient representativeWebClient;

  private final ObservableList<RepresentativeTableItem> tableItems;
  private final FilteredTableView<RepresentativeTableItem> tableView;

  public RepresentativeTableView(CustomerWebClient customerWebClient, RepresentativeWebClient representativeWebClient) {
    this.customerWebClient = Objects.requireNonNull(customerWebClient);
    this.representativeWebClient = Objects.requireNonNull(representativeWebClient);

    tableView = new FilteredTableView<>();
    getChildren().add(tableView);

    tableItems = FXCollections.observableArrayList();
    addItems();
    setUpTableView();
    FilteredTableView.configureForFiltering(tableView, tableItems);
  }

  public void refreshItems() {
    tableItems.clear();
    addItems();
  }

  private void addItems() {
    Platform.runLater(() -> fetchRepresentatives().forEach(this::addItem));
  }

  private Stream<Representative> fetchRepresentatives() {
    try {
      Representative[] representatives = Objects.requireNonNull(representativeWebClient.fetchAllRepresentatives().block());
      return Arrays.stream(representatives).sorted(Comparator.comparing(Representative::getLastName));
    } catch (Exception e) {
      throw new FetchException("""
          Nepodarilo sa získať dáta o reprezentantoch.
          Preverte spojenie so serverom.""", e);
    }
  }

  private void addItem(Representative representative) {
    var item = new RepresentativeTableItem(representative);
    tableItems.add(item);
  }

  public void addItem(RepresentativeTableItem item) {
    tableItems.add(item);
  }

  private void setUpTableView() {
    DesktopUtil.addColumn("Meno",
        Pos.CENTER_LEFT, tableView, String.class, RepresentativeTableItem::firstNameProperty);
    DesktopUtil.addColumn("Priezvisko", tableView, String.class, RepresentativeTableItem::lastNameProperty);
    DesktopUtil.addColumn("Spoločnosť", tableView, String.class, RepresentativeTableItem::customerProperty);
    DesktopUtil.addColumn("Pozícia", tableView, String.class, RepresentativeTableItem::positionProperty);
    DesktopUtil.addColumn("Región", tableView, String.class, RepresentativeTableItem::regionProperty);
    DesktopUtil.addColumn("Tel. číslo", tableView, String.class, RepresentativeTableItem::phoneNumbersProperty);
    DesktopUtil.addColumn("Email", tableView, String.class, RepresentativeTableItem::emailsProperty);
    DesktopUtil.addColumn("Stav", tableView, Status.class, RepresentativeTableItem::statusProperty);
    DesktopUtil.addColumn("Posledné stretnutie",
        tableView, RonjaDate.class, RepresentativeTableItem::lastVisitProperty);
    DesktopUtil.addColumn("Plánované stretnutie",
        tableView, RonjaDate.class, RepresentativeTableItem::scheduledVisitProperty);
    DesktopUtil.addColumn("Poznámka", tableView, String.class, RepresentativeTableItem::noticeProperty);

    tableView.setContextMenu(setUpContextMenu());
    VBox.setVgrow(tableView, Priority.ALWAYS);
  }

  public ReadOnlyObjectProperty<RepresentativeTableItem> selectedRepresentative() {
    return tableView.getSelectionModel().selectedItemProperty();
  }

  private BooleanBinding isSelectedRepresentativeNull() {
    return Bindings.isNull(selectedRepresentative());
  }

  private ContextMenu setUpContextMenu() {
    // reset all filters
    var resetFiltersItem = new MenuItem("Zrušiť filtrovanie");
    resetFiltersItem.setOnAction(e -> DesktopUtil.resetFilters(tableView));
    // fetch all items from
    var refreshItem = new MenuItem("Obnoviť");
    refreshItem.setOnAction(e -> refreshItems());
    // update selected representative
    var updateItem = new MenuItem("Upraviť...");
    updateItem.setOnAction(e -> Dialogs.showRepresentativeDetailDialog(
        customerWebClient, representativeWebClient, this, true));
    updateItem.disableProperty().bind(isSelectedRepresentativeNull());
    // add new representative
    var addItem = new MenuItem("Pridať nového...");
    addItem.setOnAction(e -> Dialogs.showRepresentativeDetailDialog(
        customerWebClient, representativeWebClient, this, false));
    // remove existing representative
    var deleteItem = new MenuItem("Zmazať...");
    //deleteItem.setOnAction(e -> deleteCustomer());
    deleteItem.disableProperty().bind(isSelectedRepresentativeNull());
    // create context menu
    var contextMenu = new ContextMenu();
    contextMenu.getItems().addAll(
        resetFiltersItem, new SeparatorMenuItem(),
        refreshItem, new SeparatorMenuItem(),
        updateItem, addItem, deleteItem);

    return contextMenu;
  }
}
