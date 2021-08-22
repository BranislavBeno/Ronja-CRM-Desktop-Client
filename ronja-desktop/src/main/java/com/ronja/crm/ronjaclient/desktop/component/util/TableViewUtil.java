package com.ronja.crm.ronjaclient.desktop.component.util;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.util.Objects;
import java.util.function.Function;

public class TableViewUtil {

    private TableViewUtil() {
    }

    public static <T, U> FilteredTableColumn<T, U> addColumn(String name, TableView<T> table, Class<U> clazz,
                                                             Function<T, ObservableValue<U>> function) {
        return addColumn(name, Pos.CENTER, table, clazz, function);
    }

    public static <T, U> FilteredTableColumn<T, U> addColumn(String caption, Pos alignment, TableView<T> table,
                                                             Class<U> clazz, Function<T, ObservableValue<U>> function) {
        FilteredTableColumn<T, U> column = new FilteredTableColumn<>(caption);
        column.setCellValueFactory(p -> function.apply(p.getValue()));
        column.setStyle(String.format("-fx-alignment: %s;", alignment));

        SouthFilter<T, U> filter = new SouthFilter<>(column, clazz);
        column.setSouthNode(filter);

        table.getColumns().add(column);

        return column;
    }

    public static <T> void refreshTableView(FilteredTableView<T> tableView) {
        tableView.getColumns().stream()
                .filter(FilteredTableColumn.class::isInstance)
                .map(FilteredTableColumn.class::cast)
                .filter(FilteredTableColumn::isFilterable)
                .map(FilteredTableColumn::getSouthNode)
                .filter(Objects::nonNull)
                .filter(SouthFilter.class::isInstance)
                .map(f -> ((SouthFilter) f).getFilterEditor())
                .forEach(f -> {
                    f.cancelFilter();
                    f.getEditor().setText("");
                });
    }
}
