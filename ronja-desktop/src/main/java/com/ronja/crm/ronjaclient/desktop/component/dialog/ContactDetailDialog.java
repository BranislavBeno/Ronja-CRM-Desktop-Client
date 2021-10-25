package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.component.representative.ContactTableItem;
import com.ronja.crm.ronjaclient.service.domain.Contact;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;

public final class ContactDetailDialog extends Dialog<ContactTableItem> {

    private final TextField contactTextField;
    private final TextField typeTextField;
    private final CheckBox primaryCheckBox;

    public ContactDetailDialog(Contact contact) {
        Objects.requireNonNull(contact);

        contactTextField = new TextField(contact.getContent());
        typeTextField = new TextField(contact.getType());
        primaryCheckBox = new CheckBox();
        primaryCheckBox.setSelected(contact.isPrimary());

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), columnConstraints);
        VBox.setVgrow(gridPane, Priority.NEVER);
        gridPane.addRow(0, new Label("Kontakt:"), contactTextField);
        gridPane.addRow(1, new Label("Typ:"), typeTextField);
        gridPane.addRow(2, new Label("Primárny:"), primaryCheckBox);
        this.getDialogPane().setContent(gridPane);

        // Set the button types.
        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        // Enable/Disable login button depending on whether a username was entered.
        this.getDialogPane().lookupButton(okButtonType);

        // Request focus on the username field by default.
        Platform.runLater(contactTextField::requestFocus);

        // Convert the result to a contact when the ok button is clicked.
        this.setResultConverter(button -> {
            if (button.equals(okButtonType)) {
                Contact newContact = new Contact(contactTextField.getText(), typeTextField.getText(), primaryCheckBox.isSelected());
                return new ContactTableItem(newContact);
            }
            return null;
        });
    }
}
