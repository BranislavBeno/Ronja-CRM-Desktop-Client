package com.ronja.crm.ronjaclient.desktop.component.customer;

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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class CustomerTableView extends VBox {

    @Value("${client.customers.base-url}")
    String baseUrl;
    @Autowired
    private final CustomerWebClient customerWebClient;

    private final ObservableList<CustomerTableItem> tableItems;
    private final FilteredTableView<CustomerTableItem> tableView;
    private final TextField searchTextField;

    public CustomerTableView(CustomerWebClient customerWebClient) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);

        this.searchTextField = new TextField();
        GridPane customerToolBar = setUpToolBar();
        tableView = new FilteredTableView<>();
        getChildren().addAll(customerToolBar, tableView);

        tableItems = FXCollections.observableArrayList();
        addItems();
        setUpTableView();
        setUpItemsFilter();
        FilteredTableView.configureForFiltering(tableView, tableItems);
    }

    private GridPane setUpToolBar() {
        var refreshButton = new Button("Obnov");
        refreshButton.setOnAction(e -> {
            searchTextField.textProperty().setValue("");
            refreshItems();
        });

        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        var gridPane = new GridPane();
        gridPane.addRow(0, new Label("Hľadať:"), searchTextField, refreshButton);
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
            Customer[] customers = Objects.requireNonNull(customerWebClient.fetchAllCustomers().block());
            return Arrays.stream(customers).sorted(Comparator.comparing(Customer::getCompanyName));
        } catch (Exception e) {
            throw new CustomerFetchException("""
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
        TableViewUtil.addColumn("Názov spoločnosti", Pos.CENTER_LEFT, tableView, String.class,
                CustomerTableItem::companyNameProperty);
        TableViewUtil.addColumn("Kategória", tableView, Category.class, CustomerTableItem::categoryProperty);
        TableViewUtil.addColumn("Zameranie", tableView, Focus.class, CustomerTableItem::focusProperty);
        TableViewUtil.addColumn("Stav", tableView, Status.class, CustomerTableItem::statusProperty);

        tableView.setContextMenu(setUpContextMenu());
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

    private ContextMenu setUpContextMenu() {
        var menuItem1 = new MenuItem("Upraviť...");
        menuItem1.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, true));
        menuItem1.disableProperty().bind(isSelectedCustomerNull());

        var menuItem2 = new MenuItem("Pridať nového...");
        menuItem2.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, false));

        var menuItem3 = new MenuItem("Zmazať...");
        menuItem3.setOnAction(e -> deleteCustomer());
        menuItem3.disableProperty().bind(isSelectedCustomerNull());

        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItem1, new SeparatorMenuItem(), menuItem2, new SeparatorMenuItem(), menuItem3);

        return contextMenu;
    }

    private BooleanBinding isSelectedCustomerNull() {
        return Bindings.isNull(selectedCustomer());
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
