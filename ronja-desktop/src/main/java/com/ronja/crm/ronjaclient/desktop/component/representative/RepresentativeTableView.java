package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.util.TableViewUtil;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.Representative;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RepresentativeTableView extends VBox {

    @Value("${client.representatives.base-url}")
    String baseUrl;
    @Autowired
    private final RepresentativeWebClient representativeWebClient;

    private final ObservableList<RepresentativeTableItem> tableItems;
    private final FilteredTableView<RepresentativeTableItem> tableView;

    public RepresentativeTableView(RepresentativeWebClient representativeWebClient) {
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

    private void setUpTableView() {
        TableViewUtil.addColumn("Meno",
                Pos.CENTER_LEFT, tableView, String.class, RepresentativeTableItem::firstNameProperty);
        TableViewUtil.addColumn("Priezvisko", tableView, String.class, RepresentativeTableItem::lastNameProperty);
        TableViewUtil.addColumn("Spoločnosť", tableView, String.class, RepresentativeTableItem::customerProperty);
        TableViewUtil.addColumn("Pozícia", tableView, String.class, RepresentativeTableItem::positionProperty);
        TableViewUtil.addColumn("Región", tableView, String.class, RepresentativeTableItem::regionProperty);
        TableViewUtil.addColumn("Tel. číslo", tableView, String.class, RepresentativeTableItem::phoneNumbersProperty);
        TableViewUtil.addColumn("Email", tableView, String.class, RepresentativeTableItem::emailsProperty);
        TableViewUtil.addColumn("Stav", tableView, Status.class, RepresentativeTableItem::statusProperty);
        TableViewUtil.addColumn("Posledné stretnutie",
                tableView, LocalDate.class, RepresentativeTableItem::lastVisitProperty);
        TableViewUtil.addColumn("Plánované stretnutie",
                tableView, LocalDate.class, RepresentativeTableItem::scheduledVisitProperty);
        TableViewUtil.addColumn("Poznámka", tableView, String.class, RepresentativeTableItem::noticeProperty);

        tableView.setContextMenu(setUpContextMenu());
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }

    public ReadOnlyObjectProperty<RepresentativeTableItem> selectedCustomer() {
        return tableView.getSelectionModel().selectedItemProperty();
    }

    private BooleanBinding isSelectedCustomerNull() {
        return Bindings.isNull(selectedCustomer());
    }

    private ContextMenu setUpContextMenu() {
        // refresh all items through resetting all filters
        var refreshItem = new MenuItem("Obnoviť");
        refreshItem.setOnAction(e -> TableViewUtil.refreshTableView(tableView));
        // update selected representative
        var updateItem = new MenuItem("Upraviť...");
        //updateItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, true));
        updateItem.disableProperty().bind(isSelectedCustomerNull());
        // add new representative
        var addItem = new MenuItem("Pridať nového...");
        //addItem.setOnAction(e -> Dialogs.showCustomerDetailDialog(customerWebClient, this, false));
        // remove existing customer
        var deleteItem = new MenuItem("Zmazať...");
        //deleteItem.setOnAction(e -> deleteCustomer());
        deleteItem.disableProperty().bind(isSelectedCustomerNull());
        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), updateItem, addItem, deleteItem);

        return contextMenu;
    }
}
