package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.desktop.component.util.TableViewUtil;
import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class CustomerTableView extends VBox {

  private final CustomerToolBar customerToolBar;
  private final ObservableList<CustomerTableItem> tableItems;
  private final TableView<CustomerTableItem> tableView;
  private final CustomerApiClient customerApiClient;

  public CustomerTableView(CustomerApiClient customerApiClient) {
    this.customerApiClient = Objects.requireNonNull(customerApiClient);

    this.customerToolBar = new CustomerToolBar();
    tableView = new TableView<>();
    getChildren().addAll(customerToolBar, tableView);

    tableItems = FXCollections.observableArrayList();
    addItems();
    setUpTableView();
    setUpItemsFilter();
  }

  private void addItems() {
    Platform.runLater(() -> fetchCustomers().forEach(this::addItem));
  }

  private Stream<Customer> fetchCustomers() {
    try {
      return Arrays.stream(customerApiClient.fetchAllCustomers())
          .sorted(Comparator.comparingInt(Customer::getId));
    } catch (Exception e) {
      throw new RuntimeException("""
          Nepodarilo sa získať dáta o klientoch.
          Preverte spojenie so serverom.""", e);
    }
  }

  private void addItem(Customer customer) {
    var item = new CustomerTableItem(customer);
    tableItems.add(item);
  }

  private void setUpTableView() {
    TableViewUtil.addColumn("Id", tableView, CustomerTableItem::idProperty);
    TableViewUtil.addColumn("Názov spoločnosti", Pos.CENTER_LEFT, tableView,
        CustomerTableItem::companyNameProperty);
    TableViewUtil.addColumn("Kategória", tableView, CustomerTableItem::categoryProperty);
    TableViewUtil.addColumn("Zameranie", tableView, CustomerTableItem::focusProperty);
    TableViewUtil.addColumn("Stav", tableView, CustomerTableItem::statusProperty);

    VBox.setVgrow(tableView, Priority.ALWAYS);
  }

  private void setUpItemsFilter() {
    FilteredList<CustomerTableItem> filteredItems = new FilteredList<>(tableItems);
    customerToolBar.getSearchTextProperty().addListener((observableValue, oldVal, newVal) ->
        Platform.runLater(() -> filteredItems.setPredicate(createPredicate(newVal))));
    SortedList<CustomerTableItem> sortedItems = new SortedList<>(filteredItems);
    sortedItems.comparatorProperty().bind(tableView.comparatorProperty());
    tableView.setItems(sortedItems);
  }

  private Predicate<CustomerTableItem> createPredicate(String searchText) {
    return item -> {
      if (searchText == null || searchText.isEmpty()) return true;
      return searchForItem(searchText, item);
    };
  }

  private boolean searchForItem(String searchText, CustomerTableItem item) {
    return setUpCriteria(searchText, item.getCompanyName())
        || setUpCriteria(searchText, item.getCategory().toString())
        || setUpCriteria(searchText, item.getFocus().toString())
        || setUpCriteria(searchText, item.statusProperty().toString());
  }

  private boolean setUpCriteria(String searchText, String s) {
    return s.contains(searchText);
  }

  public ReadOnlyObjectProperty<CustomerTableItem> selectedCustomer() {
    return tableView.getSelectionModel().selectedItemProperty();
  }
}
