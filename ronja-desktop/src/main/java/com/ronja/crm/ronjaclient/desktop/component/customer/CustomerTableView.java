package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.desktop.component.common.AppInfo;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Status;
import com.ronja.crm.ronjaclient.service.validation.DeleteException;
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
    private final AppInfo appInfo;

    public CustomerTableView(CustomerWebClient customerWebClient,
                             RepresentativeTableView representativeTableView,
                             AppInfo appInfo) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeTableView = Objects.requireNonNull(representativeTableView);
        this.appInfo = Objects.requireNonNull(appInfo);

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
        DesktopUtil.addFilteredColumn(I18N.get("customer.company.name"),
                Pos.CENTER_LEFT, tableView, String.class, CustomerTableItem::companyNameProperty);
        DesktopUtil.addFilteredColumn(I18N.get("customer.category.name"), tableView, Category.class, CustomerTableItem::categoryProperty);
        DesktopUtil.addFilteredColumn(I18N.get("customer.focus.name"), tableView, Focus.class, CustomerTableItem::focusProperty);
        DesktopUtil.addFilteredColumn(I18N.get("label.dialog.state"), tableView, Status.class, CustomerTableItem::statusProperty);

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
        var resetFiltersItem = I18nUtils.menuItemForValue("label.clear.filters");
        resetFiltersItem.setOnAction(_ -> DesktopUtil.resetFilters(tableView));
        // fetch all items from
        var refreshItem = I18nUtils.menuItemForValue("label.reload.items");
        refreshItem.setOnAction(_ -> refreshItems());
        // update selected customer
        var updateItem = new MenuItem(getCaption("label.modify.item"));
        updateItem.setOnAction(_ -> Dialogs.showCustomerDetailDialog(customerWebClient, this, representativeTableView, true));
        updateItem.disableProperty().bind(isSelectedCustomerNull());
        // add new customer
        var addItem = new MenuItem(getCaption("label.add.new.item"));
        addItem.setOnAction(_ -> Dialogs.showCustomerDetailDialog(customerWebClient, this, representativeTableView, false));
        // remove existing customer
        var deleteItem = new MenuItem(getCaption("label.remove.item"));
        deleteItem.setOnAction(_ -> deleteCustomer());
        deleteItem.disableProperty().bind(isSelectedCustomerNull());
        // show application info
        var aboutItem = new MenuItem(getCaption("label.about.info"));
        aboutItem.setOnAction(_ -> Dialogs.showAboutDialog(appInfo));

        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                resetFiltersItem, new SeparatorMenuItem(),
                refreshItem, new SeparatorMenuItem(),
                updateItem, addItem, deleteItem, new SeparatorMenuItem(),
                aboutItem);

        return contextMenu;
    }

    private static String getCaption(String key) {
        return I18N.get(key) + "...";
    }

    private void deleteCustomer() {
        CustomerTableItem customerItem = selectedCustomer().get();
        var title = I18N.get("customer.delete.title");
        var message = I18N.get("customer.delete.message")
                .formatted(customerItem.companyNameProperty().get());
        if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
            try {
                CompletableFuture<Void> cf = CompletableFuture
                        .runAsync(() -> deleteCustomer(customerItem))
                        .whenComplete((_, t) -> deleteCustomerItem(customerItem, t));
                cf.get();
            } catch (Exception _) {
                Thread.currentThread().interrupt();
                throw new DeleteException(I18N.get("exception.customer.delete")
                        + System.lineSeparator()
                        + I18N.get("exception.customer.delete.representative")
                        + System.lineSeparator()
                        + I18N.get("exception.server.connection.or"));
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
