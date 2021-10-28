package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.DeleteException;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.*;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class RepresentativeTableView extends VBox {

    private final CustomerWebClient customerWebClient;
    private final RepresentativeWebClient representativeWebClient;
    private final RepresentativeMapper mapper;
    private final boolean forDialog;
    private Customer customer = new Customer();

    private final ObservableList<RepresentativeTableItem> tableItems;
    private final FilteredTableView<RepresentativeTableItem> tableView;

    public RepresentativeTableView(CustomerWebClient customerWebClient,
                                   RepresentativeWebClient representativeWebClient,
                                   RepresentativeMapper mapper) {
        this(customerWebClient, representativeWebClient, mapper, false);
    }

    public RepresentativeTableView(CustomerWebClient customerWebClient,
                                   RepresentativeWebClient representativeWebClient,
                                   RepresentativeMapper mapper,
                                   boolean forDialog) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeWebClient = Objects.requireNonNull(representativeWebClient);
        this.mapper = Objects.requireNonNull(mapper);
        this.forDialog = forDialog;

        tableView = new FilteredTableView<>();
        getChildren().add(tableView);

        tableItems = FXCollections.observableArrayList();
        addItems();
        setUpTableView();
        FilteredTableView.configureForFiltering(tableView, tableItems);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
            int customerId = customer != null ? customer.getId() : -1;
            Representative[] representatives = switch (customerId) {
                case -1 -> new Representative[]{};
                case 0 -> Objects.requireNonNull(representativeWebClient.fetchAllRepresentatives().block());
                default -> Objects.requireNonNull(representativeWebClient.fetchParticularRepresentatives(customerId).block());
            };
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
        DesktopUtil.addFilteredColumn("Meno",
                Pos.CENTER_LEFT, tableView, String.class, RepresentativeTableItem::firstNameProperty);
        DesktopUtil.addFilteredColumn("Priezvisko", tableView, String.class, RepresentativeTableItem::lastNameProperty);
        DesktopUtil.addFilteredColumn("Spoločnosť", tableView, String.class, RepresentativeTableItem::customerProperty);
        DesktopUtil.addFilteredColumn("Pozícia", tableView, String.class, RepresentativeTableItem::positionProperty);
        DesktopUtil.addFilteredColumn("Región", tableView, String.class, RepresentativeTableItem::regionProperty);
        DesktopUtil.addFilteredColumn("Tel. číslo", tableView, String.class, RepresentativeTableItem::phoneNumbersProperty);
        DesktopUtil.addFilteredColumn("Email", tableView, String.class, RepresentativeTableItem::emailsProperty);
        DesktopUtil.addFilteredColumn("Stav", tableView, Status.class, RepresentativeTableItem::statusProperty);
        DesktopUtil.addFilteredColumn("Spôsob kontaktovania", tableView, ContactType.class, RepresentativeTableItem::contactTypeProperty);
        DesktopUtil.addFilteredColumn("Posledné stretnutie",
                tableView, RonjaDate.class, RepresentativeTableItem::lastVisitProperty);
        DesktopUtil.addFilteredColumn("Plánované stretnutie",
                tableView, RonjaDate.class, RepresentativeTableItem::scheduledVisitProperty);
        DesktopUtil.addFilteredColumn("Poznámka", tableView, String.class, RepresentativeTableItem::noticeProperty);

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
        // menu item for reset all filters
        MenuItem resetFiltersItem = provideMenuItem("Odstrániť filtre", e -> DesktopUtil.resetFilters(tableView));
        // menu item for fetch all items from
        MenuItem refreshItem = provideMenuItem("Znovu načítať zoznam", e -> refreshItems());
        // menu item for update selected representative
        MenuItem updateItem = provideBoundMenuItem("Upraviť...", e -> Dialogs.showRepresentativeDetailDialog(
                customerWebClient, representativeWebClient, this, mapper, true, forDialog));
        // menu item for add new representative
        MenuItem addItem = provideMenuItem("Pridať nového...", e -> Dialogs.showRepresentativeDetailDialog(
                customerWebClient, representativeWebClient, this, mapper, false, forDialog));
        // menu item for remove existing representative
        MenuItem deleteItem = provideBoundMenuItem("Zmazať...", e -> deleteRepresentative());

        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                resetFiltersItem, new SeparatorMenuItem(),
                refreshItem, new SeparatorMenuItem(),
                updateItem, addItem, deleteItem);

        return contextMenu;
    }

    private MenuItem provideMenuItem(String title, EventHandler<ActionEvent> value) {
        var item = new MenuItem(title);
        item.setOnAction(value);

        return item;
    }

    private MenuItem provideBoundMenuItem(String title, EventHandler<ActionEvent> value) {
        var item = provideMenuItem(title, value);
        item.disableProperty().bind(isSelectedRepresentativeNull());

        return item;
    }

    private void deleteRepresentative() {
        RepresentativeTableItem representativeItem = selectedRepresentative().get();
        var title = "Zmazať reprezentanta";
        var message = "Skutočne chcete zmazať reprezentanta '%s %s'?".formatted(
                representativeItem.firstNameProperty().get(), representativeItem.lastNameProperty().get());
        if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
            try {
                CompletableFuture<Void> cf = CompletableFuture
                        .runAsync(() -> deleteRepresentative(representativeItem))
                        .whenComplete((r, t) -> deleteRepresentativeItem(representativeItem, t));
                cf.get();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new DeleteException("""
                        Zmazanie reprezentanta zlyhalo.
                        Preverte spojenie so serverom.""");
            }
        }
    }

    private void deleteRepresentative(RepresentativeTableItem representativeItem) {
        int id = representativeItem.getRepresentative().getId();
        representativeWebClient.deleteRepresentative(id).block();
    }

    private void deleteRepresentativeItem(RepresentativeTableItem representativeItem, Throwable throwable) {
        if (throwable == null) {
            tableItems.remove(representativeItem);
        }
    }
}
