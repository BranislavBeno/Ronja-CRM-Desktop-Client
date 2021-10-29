package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.DeleteException;
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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CustomerTableView extends VBox {

    private final CustomerWebClient customerWebClient;
    private final RepresentativeTableView representativeTableView;
    private final ObservableList<CustomerTableItem> tableItems;
    private final FilteredTableView<CustomerTableItem> tableView;

    public CustomerTableView(CustomerWebClient customerWebClient,
                             RepresentativeTableView representativeTableView) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeTableView = Objects.requireNonNull(representativeTableView);

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
        Platform.runLater(() -> DesktopUtil.fetchCustomers(customerWebClient).forEach(this::addItem));
    }

    private void addItem(Customer customer) {
        var item = new CustomerTableItem(customer);
        addItem(item);
    }

    public void addItem(CustomerTableItem item) {
        tableItems.add(item);
    }

    private void setUpTableView() {
        DesktopUtil.addFilteredColumn("Názov spoločnosti",
                Pos.CENTER_LEFT, tableView, String.class, CustomerTableItem::companyNameProperty);
        DesktopUtil.addFilteredColumn("Kategória", tableView, Category.class, CustomerTableItem::categoryProperty);
        DesktopUtil.addFilteredColumn("Zameranie", tableView, Focus.class, CustomerTableItem::focusProperty);
        DesktopUtil.addFilteredColumn("Stav", tableView, Status.class, CustomerTableItem::statusProperty);

        tableView.setContextMenu(setUpContextMenu());
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }

    public ReadOnlyObjectProperty<CustomerTableItem> selectedCustomer() {
        return tableView.getSelectionModel().selectedItemProperty();
    }

    private BooleanBinding isSelectedCustomerNull() {
        return Bindings.isNull(selectedCustomer());
    }

    private ContextMenu setUpContextMenu() {
        // reset all filters
        var resetFiltersItem = new MenuItem("Odstrániť filtre");
        resetFiltersItem.setOnAction(e -> DesktopUtil.resetFilters(tableView));
        // fetch all items from
        var refreshItem = new MenuItem("Znovu načítať zoznam");
        refreshItem.setOnAction(e -> refreshItems());
        // update selected customer
        var updateItem = new MenuItem("Upraviť...");
        updateItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, representativeTableView, true));
        updateItem.disableProperty().bind(isSelectedCustomerNull());
        // add new customer
        var addItem = new MenuItem("Pridať nového...");
        addItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, representativeTableView, false));
        // remove existing customer
        var deleteItem = new MenuItem("Zmazať...");
        deleteItem.setOnAction(e -> deleteCustomer());
        deleteItem.disableProperty().bind(isSelectedCustomerNull());
        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                resetFiltersItem, new SeparatorMenuItem(),
                refreshItem, new SeparatorMenuItem(),
                updateItem, addItem, deleteItem);

        return contextMenu;
    }


    private void deleteCustomer() {
        CustomerTableItem customerItem = selectedCustomer().get();
        var title = "Zmazať zákazníka";
        var message = String.format("Skutočne chcete zmazať zákazníka '%s'?",
                customerItem.companyNameProperty().get());
        if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
            try {
                CompletableFuture<Void> cf = CompletableFuture
                        .runAsync(() -> deleteCustomer(customerItem))
                        .whenComplete((r, t) -> deleteCustomerItem(customerItem, t));
                cf.get();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new DeleteException("""
                        Zmazanie zákazníka zlyhalo.
                        Zmažte najskôr reprezentantov uvedeného zákazníka,
                        alebo preverte spojenie so serverom.""");
            }
        }
    }

    private void deleteCustomer(CustomerTableItem customerItem) {
        int id = customerItem.getCustomer().getId();
        customerWebClient.deleteCustomer(id).block();
    }

    private void deleteCustomerItem(CustomerTableItem customerItem, Throwable throwable) {
        if (throwable == null) {
            tableItems.remove(customerItem);
        }
    }
}
