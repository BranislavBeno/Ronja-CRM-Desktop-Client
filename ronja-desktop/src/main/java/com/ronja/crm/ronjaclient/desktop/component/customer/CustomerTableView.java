package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.util.TableViewUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Component
public class CustomerTableView extends VBox {

  @Value("${client.customers.base-url}")
  String baseUrl;
  @Autowired
  private final CustomerWebClient customerWebClient;

  private final ObservableList<CustomerTableItem> tableItems;
  private final FilteredTableView<CustomerTableItem> tableView;

  public CustomerTableView(CustomerWebClient customerWebClient) {
    this.customerWebClient = Objects.requireNonNull(customerWebClient);

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
    Platform.runLater(() -> fetchCustomers().forEach(this::addItem));
  }


  private Stream<Customer> fetchCustomers() {
    try {
      Customer[] customers = Objects.requireNonNull(customerWebClient.fetchAllCustomers().block());
      return Arrays.stream(customers).sorted(Comparator.comparing(Customer::getCompanyName));
    } catch (Exception e) {
      throw new FetchException("""
          Nepodarilo sa získať dáta o klientoch.
          Preverte spojenie so serverom.""", e);
    }
  }

  private void addItem(Customer customer) {
    var item = new CustomerTableItem(customer);
    addItem(item);
  }

  public void addItem(CustomerTableItem item) {
    tableItems.add(item);
  }

  private void setUpTableView() {
    TableViewUtil.addColumn("Názov spoločnosti",
        Pos.CENTER_LEFT, tableView, String.class, CustomerTableItem::companyNameProperty);
    TableViewUtil.addColumn("Kategória", tableView, Category.class, CustomerTableItem::categoryProperty);
    TableViewUtil.addColumn("Zameranie", tableView, Focus.class, CustomerTableItem::focusProperty);
    TableViewUtil.addColumn("Stav", tableView, Status.class, CustomerTableItem::statusProperty);

    tableView.setContextMenu(setUpContextMenu());
    VBox.setVgrow(tableView, Priority.ALWAYS);
  }

  public ReadOnlyObjectProperty<CustomerTableItem> selectedCustomer() {
    return tableView.getSelectionModel().selectedItemProperty();
  }

  private ContextMenu setUpContextMenu() {
    var refreshItem = new MenuItem("Obnoviť");
    refreshItem.setOnAction(e -> refreshTableView());

    var updateItem = new MenuItem("Upraviť...");
    updateItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, true));
    updateItem.disableProperty().bind(isSelectedCustomerNull());

    var addItem = new MenuItem("Pridať nového...");
    addItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, false));

    var deleteItem = new MenuItem("Zmazať...");
    deleteItem.setOnAction(e -> deleteCustomer());
    deleteItem.disableProperty().bind(isSelectedCustomerNull());

    var contextMenu = new ContextMenu();
    contextMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), updateItem, addItem, deleteItem);

    return contextMenu;
  }

  private BooleanBinding isSelectedCustomerNull() {
    return Bindings.isNull(selectedCustomer());
  }

  private void refreshTableView() {
    tableView.resetFilter();
  }

  private void deleteCustomer() {
    CustomerTableItem customerItem = selectedCustomer().get();
    var title = "Zmazanie zákazníka";
    var message = String.format("Skutočne chcete zmazať zákazníka '%s'?",
        customerItem.companyNameProperty().get());
    if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
      CompletableFuture
          .runAsync(() -> {
            int id = customerItem.getCustomer().getId();
            customerWebClient.deleteCustomer(id).block();
          })
          .whenComplete((r, t) -> {
            if (t == null) {
              Platform.runLater(() -> tableItems.remove(customerItem));
            } else {
              refreshItems();
            }
          });
    }
  }
}
