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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomerTableView extends VBox {

    private final ObservableList<CustomerTableItem> tableItems;
    private final TableView<CustomerTableItem> tableView;
    private final CustomerApiClient customerApiClient;
    private final TextField searchTextField;

    public CustomerTableView(CustomerApiClient customerApiClient) {
        this.customerApiClient = Objects.requireNonNull(customerApiClient);

        this.searchTextField = new TextField();
        GridPane customerToolBar = setUpToolBar();
        tableView = new TableView<>();
        getChildren().addAll(customerToolBar, tableView);

        tableItems = FXCollections.observableArrayList();
        addItems();
        setUpTableView();
        setUpItemsFilter();
    }

    private GridPane setUpToolBar() {
        var refreshButton = new Button("Obnov");
        refreshButton.setOnAction(e -> {
            searchTextField.textProperty().setValue("");
            refreshItems();
        });

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        var gridPane = new GridPane();
        gridPane.addRow(0, new Label("Hľadaj:"), searchTextField, refreshButton);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints, new ColumnConstraints());
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(5);
        gridPane.setPadding(new Insets(12, 5, 12, 5));
        HBox.setHgrow(searchTextField, Priority.ALWAYS);

        return gridPane;
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
            return Arrays.stream(customerApiClient.fetchAllCustomers())
                    .sorted(Comparator.comparing(Customer::getCompanyName));
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
        TableViewUtil.addColumn("Názov spoločnosti", Pos.CENTER_LEFT, tableView,
                CustomerTableItem::companyNameProperty);
        TableViewUtil.addColumn("Kategória", tableView, CustomerTableItem::categoryProperty);
        TableViewUtil.addColumn("Zameranie", tableView, CustomerTableItem::focusProperty);
        TableViewUtil.addColumn("Stav", tableView, CustomerTableItem::statusProperty);

        VBox.setVgrow(tableView, Priority.ALWAYS);
    }

    private void setUpItemsFilter() {
        FilteredList<CustomerTableItem> filteredItems = new FilteredList<>(tableItems);
        searchTextField.textProperty().addListener((observableValue, oldVal, newVal) ->
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

    public ObservableList<CustomerTableItem> getTableItems() {
        return tableItems;
    }
}
