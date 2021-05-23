package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerSplitPane;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    @FXML
    private Tab customersTab;

    @Autowired
    private final CustomerSplitPane customerSplitPane;

    public MainWindowController(CustomerSplitPane customerSplitPane) {
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

        this.customerSplitPane = customerSplitPane;
    }

    @FXML
    public void initialize() {
        customersTab.setContent(customerSplitPane);
    }

    private void handleUncaughtException(Thread thread, Throwable throwable) {
        Platform.runLater(
                () -> Dialogs.showErrorMessage("Chyba", throwable.getMessage()));
    }
}
