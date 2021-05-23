package com.ronja.crm.ronjaclient.desktop.component.util;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.Function;

public class TableViewUtil {

    private TableViewUtil() {
    }

    public static <T, S> TableColumn<T, S> addColumn(String name, TableView<T> table, Function<T, ObservableValue<S>> function) {
        return addColumn(name, Pos.CENTER, table, function);
    }

    public static <T, S> TableColumn<T, S> addColumn(String name, Pos alignment, TableView<T> table,
                                                     Function<T, ObservableValue<S>> function) {
        var column = new TableColumn<T, S>(name);
        column.setCellValueFactory(feature -> function.apply(feature.getValue()));
        column.setStyle(String.format("-fx-alignment: %s;", alignment));

        table.getColumns().add(column);
        return column;
    }
}
