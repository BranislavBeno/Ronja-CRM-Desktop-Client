package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.dialog.ContactDetailDialog;
import com.ronja.crm.ronjaclient.service.domain.Contact;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import java.util.Optional;

public class RonjaListView extends ListView<Contact> {

  public RonjaListView() {
    setPrefHeight(55);
    setEditable(true);
    setContextMenu(setUpContextMenu());
  }

  private ContextMenu setUpContextMenu() {
    // update selected item
    var updateItem = new MenuItem("Upraviť...");
    updateItem.setOnAction(e -> {
      Optional<Contact> result = provideInputDialog("Upraviť kontakt", selectedItem().get());
      result.ifPresent(contact -> getItems().set(getSelectionModel().getSelectedIndex(), contact));
    });
    updateItem.disableProperty().bind(isSelectedItemNull());
    // add new item
    var addItem = new MenuItem("Pridať...");
    addItem.setOnAction(e -> {
      Optional<Contact> result = provideInputDialog();
      result.ifPresent(contact -> getItems().add(getItems().size(), contact));
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

  private Optional<Contact> provideInputDialog() {
    return provideInputDialog("Pridať kontakt", new Contact());
  }

  private Optional<Contact> provideInputDialog(String title, Contact contact) {
    var contactDialog = new ContactDetailDialog(contact);
    contactDialog.setTitle(title);
    return contactDialog.showAndWait();
  }

  public ReadOnlyObjectProperty<Contact> selectedItem() {
    return getSelectionModel().selectedItemProperty();
  }

  private BooleanBinding isSelectedItemNull() {
    return Bindings.isNull(selectedItem());
  }
}
