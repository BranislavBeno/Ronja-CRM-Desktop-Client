package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.dialog.ContactDetailDialog;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.util.DesktopUtil;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.domain.Contact;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ContactTableView extends TableView<ContactTableItem> {

    private final ObservableList<ContactTableItem> tableItems;

    public ContactTableView() {
        tableItems = FXCollections.observableArrayList();
        setItems(tableItems);
        setUpTableView();
    }

    public List<Contact> getContacts() {
        return tableItems.stream()
                .map(ContactTableItem::getContact)
                .toList();
    }

    public void addContacts(List<Contact> contacts) {
        contacts.forEach(this::addItem);
    }

    private void addItem(Contact contact) {
        var item = new ContactTableItem(contact);
        addItem(item);
    }

    public void addItem(ContactTableItem item) {
        tableItems.add(item);
    }

    private void setUpTableView() {
        DesktopUtil.addColumn(I18N.get("representative.contact.label"), Pos.CENTER_LEFT, this, ContactTableItem::contentProperty);
        DesktopUtil.addColumn(I18N.get("representative.contact.type"), Pos.CENTER_LEFT, this, ContactTableItem::typeProperty);
        DesktopUtil.addColumn(I18N.get("representative.contact.primary"), Pos.CENTER, this, ContactTableItem::primaryProperty);

        setPrefHeight(98);
        setContextMenu(setUpContextMenu());
    }

    private ContextMenu setUpContextMenu() {
        // update selected item
        var updateItem = new MenuItem(I18N.get("label.modify.item") + "...");
        updateItem.disableProperty().bind(isSelectedItemNull());
        updateItem.setOnAction(_ -> {
            Contact contact = selectedItem().get().getContact();
            Optional<ContactTableItem> result = provideInputDialog(I18N.get("representative.contact.modify"), contact);
            result.ifPresent(item -> handleContact(item, this::updateContact, this::updatePriorityContact));
        });

        // add new item
        var addItem = new MenuItem(I18N.get("label.add.item") + "...");
        addItem.setOnAction(_ -> {
            Optional<ContactTableItem> result = provideInputDialog(I18N.get("representative.contact.add"), new Contact());
            result.ifPresent(contact -> handleContact(contact, this::addNewContact, this::addNewPriorityContact));
        });

        // remove existing item
        var deleteItem = new MenuItem(I18N.get("label.remove.item") + "...");
        deleteItem.disableProperty().bind(isSelectedItemNull());
        deleteItem.setOnAction(_ -> {
            ContactTableItem contact = selectedItem().get();
            String message = I18N.get("representative.contact.approve.remove").formatted(contact.getContent());
            if (Dialogs.showAlertDialog(I18N.get("representative.contact.remove"), message, Alert.AlertType.CONFIRMATION)) {
                tableItems.remove(contact);
            }
        });

        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(updateItem, addItem, deleteItem);

        return contextMenu;
    }

    private void addNewPriorityContact(ContactTableItem contactItem) {
        addNewContact(contactItem);
        refresh();
    }

    private void addNewContact(ContactTableItem contactItem) {
        tableItems.add(contactItem);
    }

    private void updatePriorityContact(ContactTableItem contactItem) {
        updateContact(contactItem);
        refresh();
    }

    private void updateContact(ContactTableItem contactItem) {
        tableItems.set(getSelectionModel().getSelectedIndex(), contactItem);
    }

    private void handleContact(ContactTableItem contactItem, Consumer<ContactTableItem> consumer, Consumer<ContactTableItem> priorityConsumer) {
        if (contactItem.isPrimary()) {
            boolean listHasPriority = tableItems.stream().anyMatch(ContactTableItem::isPrimary);
            if (listHasPriority) {
                confirmAction(contactItem, priorityConsumer);
            } else {
                consumer.accept(contactItem);
            }
        } else {
            consumer.accept(contactItem);
        }
    }

    private void confirmAction(ContactTableItem contact, Consumer<ContactTableItem> consumer) {
        String title = I18N.get("representative.contact.primary.change");
        String message = I18N.get("representative.contact.primary.exists")
                + System.lineSeparator()
                + I18N.get("representative.contact.primary.question").formatted(contact.getContent());
        if (Dialogs.showAlertDialog(title, message, Alert.AlertType.CONFIRMATION)) {
            tableItems.forEach(i -> i.setPrimary(false));
        } else {
            contact.setPrimary(false);
        }
        consumer.accept(contact);
    }

    private Optional<ContactTableItem> provideInputDialog(String title, Contact contact) {
        var contactDialog = new ContactDetailDialog(contact);
        contactDialog.setTitle(title);
        return contactDialog.showAndWait();
    }

    public ReadOnlyObjectProperty<ContactTableItem> selectedItem() {
        return getSelectionModel().selectedItemProperty();
    }

    private BooleanBinding isSelectedItemNull() {
        return Bindings.isNull(selectedItem());
    }
}
