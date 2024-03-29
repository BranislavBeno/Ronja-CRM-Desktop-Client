package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.common.AppInfo;
import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.dialog.DateFilterDialog;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.*;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import com.ronja.crm.ronjaclient.service.validation.DeleteException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepresentativeTableView extends VBox {

    public static final String LAST_VISIT_TEXT = I18N.get("filter.recent.visits");
    public static final String SCHEDULED_VISIT_TEXT = I18N.get("filter.scheduled.visits");

    private final CustomerWebClient customerWebClient;
    private final RepresentativeWebClient representativeWebClient;
    private final RepresentativeMapper mapper;
    private final AppInfo appInfo;
    private final boolean forDialog;
    private final ObservableList<RepresentativeTableItem> tableItems;
    private final FilteredTableView<RepresentativeTableItem> tableView;
    private final HBox enhancedFilterPane;
    private Customer customer = new Customer();
    private DateRecord visitedRecord;
    private DateRecord scheduledRecord;

    public RepresentativeTableView(CustomerWebClient customerWebClient,
                                   RepresentativeWebClient representativeWebClient,
                                   RepresentativeMapper mapper,
                                   AppInfo appInfo) {
        this(customerWebClient, representativeWebClient, mapper, appInfo, false);
    }

    public RepresentativeTableView(CustomerWebClient customerWebClient,
                                   RepresentativeWebClient representativeWebClient,
                                   RepresentativeMapper mapper,
                                   AppInfo appInfo,
                                   boolean forDialog) {
        this.customerWebClient = Objects.requireNonNull(customerWebClient);
        this.representativeWebClient = Objects.requireNonNull(representativeWebClient);
        this.mapper = Objects.requireNonNull(mapper);
        this.appInfo = Objects.requireNonNull(appInfo);
        this.forDialog = forDialog;
        this.tableItems = FXCollections.observableArrayList();
        this.tableView = new FilteredTableView<>();
        this.visitedRecord = new DateRecord();
        this.scheduledRecord = new DateRecord();

        this.enhancedFilterPane = new HBox();
        getChildren().addAll(enhancedFilterPane, tableView);

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
                default ->
                        Objects.requireNonNull(representativeWebClient.fetchParticularRepresentatives(customerId).block());
            };
            return Arrays.stream(representatives).sorted(Comparator.comparing(Representative::getLastName));
        } catch (Exception e) {
            throw new FetchException("""
                    %s
                    %s""".formatted(
                    I18N.get("exception.representative.fetch"),
                    I18N.get("exception.server.connection")
            ),
                    e);
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
        DesktopUtil.addFilteredColumn(I18N.get("representative.first.name"),
                Pos.CENTER_LEFT, tableView, String.class, RepresentativeTableItem::firstNameProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.sure.name"),
                tableView, String.class, RepresentativeTableItem::lastNameProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.company.name"),
                tableView, String.class, RepresentativeTableItem::customerProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.position.name"),
                tableView, String.class, RepresentativeTableItem::positionProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.region.name"),
                tableView, String.class, RepresentativeTableItem::regionProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.phone.name"),
                tableView, String.class, RepresentativeTableItem::phoneNumbersProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.email.name"),
                tableView, String.class, RepresentativeTableItem::emailsProperty);
        DesktopUtil.addFilteredColumn(I18N.get("label.dialog.state"),
                tableView, Status.class, RepresentativeTableItem::statusProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.contacted.by"),
                tableView, ContactType.class, RepresentativeTableItem::contactTypeProperty);
        FilteredTableColumn<RepresentativeTableItem, RonjaDate> visitedColumn =
                DesktopUtil.addFilteredColumn(I18N.get("representative.last.visit"),
                        tableView, RonjaDate.class, RepresentativeTableItem::lastVisitProperty);
        FilteredTableColumn<RepresentativeTableItem, RonjaDate> scheduledColumn =
                DesktopUtil.addFilteredColumn(I18N.get("representative.scheduled.visit"),
                        tableView, RonjaDate.class, RepresentativeTableItem::scheduledVisitProperty);
        DesktopUtil.addFilteredColumn(I18N.get("representative.note.name"),
                tableView, String.class, RepresentativeTableItem::noticeProperty);

        visitedColumn.setComparator(Comparator.comparing(RonjaDate::date));
        scheduledColumn.setComparator(Comparator.comparing(RonjaDate::date));

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
        // menu item for enhanced filtering - visited
        Menu visitedFilterItem = provideVisitedFilterSubMenu();
        // menu item for enhanced filtering - scheduled
        Menu scheduledFilterItem = provideScheduledFilterSubMenu();
        // menu item for reset all filters
        MenuItem resetFiltersItem = provideMenuItem(I18N.get("label.clear.filters"), e -> {
            resetEnhancedFilter();
            DesktopUtil.resetFilters(tableView);
        });
        // menu item for fetch all items from
        MenuItem refreshItem = provideMenuItem(I18N.get("label.reload.items"), e -> refreshItems());
        // menu item for update selected representative
        MenuItem updateItem = provideBoundMenuItem(I18N.get("label.modify.item") + "...", e -> Dialogs.showRepresentativeDetailDialog(
                customerWebClient, representativeWebClient, this, mapper, true, forDialog));
        // menu item for add new representative
        MenuItem addItem = provideMenuItem(I18N.get("label.add.new.item") + "...", e -> Dialogs.showRepresentativeDetailDialog(
                customerWebClient, representativeWebClient, this, mapper, false, forDialog));
        // menu item for remove existing representative
        MenuItem deleteItem = provideBoundMenuItem(I18N.get("label.remove.item") + "...", e -> deleteRepresentative());
        // show application info
        var aboutItem = new MenuItem(I18N.get("label.about.info") + "...");
        aboutItem.setOnAction(e -> Dialogs.showAboutDialog(appInfo));

        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                visitedFilterItem, scheduledFilterItem, resetFiltersItem, new SeparatorMenuItem(),
                refreshItem, new SeparatorMenuItem(),
                updateItem, addItem, deleteItem, new SeparatorMenuItem(),
                aboutItem);

        return contextMenu;
    }

    private Menu provideVisitedFilterSubMenu() {
        MenuItem lastMonthItem = provideMenuItem(I18N.get("representative.filter.visited.1"), e -> {
            DateRecord dateRecord = toLastDateInterval(1);
            runEnhancedVisitedFilter(dateRecord);
        });
        MenuItem lastThreeMonthsItem = provideMenuItem(I18N.get("representative.filter.visited.2"), e -> {
            DateRecord dateRecord = toLastDateInterval(3);
            runEnhancedVisitedFilter(dateRecord);
        });
        MenuItem lastSixMonthsItem = provideMenuItem(I18N.get("representative.filter.visited.3"), e -> {
            DateRecord dateRecord = toLastDateInterval(6);
            runEnhancedVisitedFilter(dateRecord);
        });
        MenuItem customItem = provideMenuItem(I18N.get("representative.filter.defined") + "...", e -> {
            Optional<DateRecord> filterDates = provideDateFilterDialog(LAST_VISIT_TEXT, visitedRecord);
            filterDates.ifPresent(this::runEnhancedVisitedFilter);
        });
        // Filter main menu
        Menu menuItem = new Menu(LAST_VISIT_TEXT);
        menuItem.getItems().addAll(lastMonthItem, lastThreeMonthsItem, lastSixMonthsItem,
                new SeparatorMenuItem(), customItem);

        return menuItem;
    }

    private Menu provideScheduledFilterSubMenu() {
        MenuItem nextMonthItem = provideMenuItem(I18N.get("representative.filter.scheduled.1"), e -> {
            DateRecord dateRecord = toNextDateInterval(1);
            runEnhancedScheduledFilter(dateRecord);
        });
        MenuItem nextThreeMonthsItem = provideMenuItem(I18N.get("representative.filter.scheduled.2"), e -> {
            DateRecord dateRecord = toNextDateInterval(3);
            runEnhancedScheduledFilter(dateRecord);
        });
        MenuItem nextSixMonthsItem = provideMenuItem(I18N.get("representative.filter.scheduled.3"), e -> {
            DateRecord dateRecord = toNextDateInterval(6);
            runEnhancedScheduledFilter(dateRecord);
        });
        MenuItem customItem = provideMenuItem(I18N.get("representative.filter.defined") + "...", e -> {
            Optional<DateRecord> scheduledFilter = provideDateFilterDialog(SCHEDULED_VISIT_TEXT, scheduledRecord);
            scheduledFilter.ifPresent(this::runEnhancedScheduledFilter);
        });
        // Filter main menu
        Menu menuItem = new Menu(SCHEDULED_VISIT_TEXT);
        menuItem.getItems().addAll(nextMonthItem, nextThreeMonthsItem, nextSixMonthsItem,
                new SeparatorMenuItem(), customItem);

        return menuItem;
    }

    private DateRecord toLastDateInterval(int months) {
        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(months);
        return new DateRecord(previous, now);
    }

    private DateRecord toNextDateInterval(int months) {
        LocalDate now = LocalDate.now();
        LocalDate next = now.plusMonths(months);
        return new DateRecord(now, next);
    }

    private void runEnhancedVisitedFilter(DateRecord dateRecord) {
        visitedRecord = new DateRecord(dateRecord);
        updateEnhancedFilter();
    }

    private void runEnhancedScheduledFilter(DateRecord dateRecord) {
        scheduledRecord = new DateRecord(dateRecord);
        updateEnhancedFilter();
    }

    private boolean isWithinDate(RonjaDate itemDate, DateRecord dateRecord) {
        return isEqualOrAfter(itemDate, dateRecord.startDate())
                && isEqualOrBefore(itemDate, dateRecord.endDate());
    }

    private boolean isEqualOrBefore(RonjaDate itemDate, LocalDate endDate) {
        LocalDate date = itemDate.date();
        return date.equals(endDate) || date.isBefore(endDate);
    }

    private boolean isEqualOrAfter(RonjaDate itemDate, LocalDate startDate) {
        LocalDate date = itemDate.date();
        return date.equals(startDate) || date.isAfter(startDate);
    }

    private void updateEnhancedFilter() {
        // filter table items
        filterTableItems();
        // set up filter info bar
        setUpFilterInfoBar();
    }

    private void setUpFilterInfoBar() {
        String label = Stream.of(
                        provideFilterPaneText(visitedRecord, I18N.get("representative.show.visited")),
                        provideFilterPaneText(scheduledRecord, I18N.get("representative.show.scheduled")))
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.joining(" - "));
        enhancedFilterPane.getChildren().setAll(new Label(label));
        setPadding(new Insets(5));
        setSpacing(5);
    }

    private void filterTableItems() {
        // set initial items state
        tableView.setItems(tableItems);
        // filter visited
        if (visitedRecord.isNotEmpty()) {
            filterWithinDateInterval(i -> isWithinDate(i.getLastVisit(), visitedRecord));
        }
        // filter scheduled
        if (scheduledRecord.isNotEmpty()) {
            filterWithinDateInterval(i -> isWithinDate(i.getScheduledVisit(), scheduledRecord));
        }
    }

    private void filterWithinDateInterval(Predicate<RepresentativeTableItem> predicate) {
        FilteredList<RepresentativeTableItem> filteredItems = new FilteredList<>(tableView.getItems());
        filteredItems.setPredicate(predicate);
        SortedList<RepresentativeTableItem> sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedItems);
    }

    private void resetEnhancedFilter() {
        visitedRecord = new DateRecord();
        scheduledRecord = new DateRecord();
        enhancedFilterPane.getChildren().clear();
        setPadding(new Insets(0));
        setSpacing(0);
        tableView.setItems(tableItems);
    }

    private String provideFilterPaneText(DateRecord dateRecord, String text) {
        return dateRecord.isNotEmpty()
                ? text.formatted(
                dateRecord.startDate().format(DateTimeUtil.DATE_TIME_FORMATTER),
                dateRecord.endDate().format(DateTimeUtil.DATE_TIME_FORMATTER))
                : "";
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

    private Optional<DateRecord> provideDateFilterDialog(String title, DateRecord dateRecord) {
        var dateFilterDialog = new DateFilterDialog(dateRecord);
        dateFilterDialog.setTitle(title);
        return dateFilterDialog.showAndWait();
    }

    private void deleteRepresentative() {
        RepresentativeTableItem representativeItem = selectedRepresentative().get();
        String title = I18N.get("representative.delete.title");
        var message = I18N.get("representative.delete.message") + "'%s %s'?".formatted(
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
                        %s
                        %s""".formatted(
                        I18N.get("exception.representative.delete"),
                        I18N.get("exception.server.connection")));
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
