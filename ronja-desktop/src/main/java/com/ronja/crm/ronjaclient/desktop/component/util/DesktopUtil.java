package com.ronja.crm.ronjaclient.desktop.component.util;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.validation.SaveException;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class DesktopUtil {

    private DesktopUtil() {
    }

    public static <T, U> FilteredTableColumn<T, U> addFilteredColumn(String name, TableView<T> table, Class<U> clazz,
                                                                     Function<T, ObservableValue<U>> function) {
        return addFilteredColumn(name, Pos.CENTER, table, clazz, function);
    }

    public static <T, U> FilteredTableColumn<T, U> addFilteredColumn(String caption, Pos alignment, TableView<T> table,
                                                                     Class<U> clazz, Function<T, ObservableValue<U>> function) {
        FilteredTableColumn<T, U> column = new FilteredTableColumn<>(caption);
        column.setCellValueFactory(p -> function.apply(p.getValue()));
        column.setStyle(String.format("-fx-alignment: %s;", alignment));

        SouthFilter<T, U> filter = new SouthFilter<>(column, clazz);
        column.setSouthNode(filter);

        table.getColumns().add(column);

        return column;
    }

    public static <T, U> TableColumn<T, U> addColumn(String caption, Pos alignment, TableView<T> table,
                                                     Function<T, ObservableValue<U>> function) {
        TableColumn<T, U> column = new TableColumn<>(caption);
        column.setCellValueFactory(p -> function.apply(p.getValue()));
        column.setStyle(String.format("-fx-alignment: %s;", alignment));
        table.getColumns().add(column);

        return column;
    }

    public static <T> void resetFilters(FilteredTableView<T> tableView) {
        tableView.getColumns().stream()
                .filter(FilteredTableColumn.class::isInstance)
                .map(FilteredTableColumn.class::cast)
                .filter(FilteredTableColumn::isFilterable)
                .map(FilteredTableColumn::getSouthNode)
                .filter(Objects::nonNull)
                .filter(SouthFilter.class::isInstance)
                .map(SouthFilter.class::cast)
                .map(SouthFilter::getFilterEditor)
                .forEach(f -> {
                    f.cancelFilter();
                    f.getEditor().setText("");
                });
    }

    public static void closeOperation(Scene scene) {
        scene.getWindow().hide();
    }

    public static Stream<Customer> fetchCustomers(CustomerWebClient webClient) {
        try {
            Customer[] customers = Objects.requireNonNull(webClient.fetchAllCustomers().block());
            return Arrays.stream(customers).sorted(Comparator.comparing(Customer::getCompanyName));
        } catch (Exception e) {
            throw new FetchException("""
                    Nepodarilo sa získať dáta o klientoch.
                    Preverte spojenie so serverom.""", e);
        }
    }

    public static void handleException(Throwable throwable) {
        String message = throwable.getCause().getMessage();
        if (message.contains("Connection refused:")) {
            message = "Spojenie so serverom je prerušené.";
        }
        throw new SaveException(message);
    }
}
