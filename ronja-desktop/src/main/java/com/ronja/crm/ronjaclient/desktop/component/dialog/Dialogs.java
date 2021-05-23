package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class Dialogs {

    private Dialogs() {
    }

    public static void showErrorMessage(String title, String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.initOwner(App.getMainWindow());

        // Resize automatically according the content
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }
}
