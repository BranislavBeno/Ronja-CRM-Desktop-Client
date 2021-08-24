package com.ronja.crm.ronjaclient.desktop.component.representative;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class RonjaListView extends ListView<String> {

  public RonjaListView() {
    setPrefHeight(55);
    setEditable(true);
    setContextMenu(setUpContextMenu());
  }

  private ContextMenu setUpContextMenu() {
    // update selected item
    var updateItem = new MenuItem("Upraviť...");
    updateItem.setOnAction(e -> {
      Optional<String> result = provideInputDialog("Upraviť položku", selectedItem().get());
      if (result.isPresent() && !result.get().isEmpty()) {
        getItems().set(getSelectionModel().getSelectedIndex(), result.get());
      }
    });
    updateItem.disableProperty().bind(isSelectedItemNull());
    // add new item
    var addItem = new MenuItem("Pridať...");
    addItem.setOnAction(e -> {
      Optional<String> result = provideInputDialog();
      if (result.isPresent() && !result.get().isEmpty()) {
        getItems().add(getItems().size(), result.get());
      }
    });
    // remove existing item
    var deleteItem = new MenuItem("Zmazať...");
    deleteItem.setOnAction(e -> getItems().remove(selectedItem().get()));
    deleteItem.disableProperty().bind(isSelectedItemNull());
    // create context menu
    var contextMenu = new ContextMenu();
    contextMenu.getItems().addAll(updateItem, addItem, deleteItem);

    return contextMenu;
  }

  private Optional<String> provideInputDialog() {
    return provideInputDialog("Pridať položku", "");
  }

  private Optional<String> provideInputDialog(String title, String content) {
    TextInputDialog textInputDialog = new TextInputDialog(content);
    textInputDialog.setTitle(title);
    textInputDialog.setHeaderText(null);
    return textInputDialog.showAndWait();
  }

  public ReadOnlyObjectProperty<String> selectedItem() {
    return getSelectionModel().selectedItemProperty();
  }

  private BooleanBinding isSelectedItemNull() {
    return Bindings.isNull(selectedItem());
  }
}
