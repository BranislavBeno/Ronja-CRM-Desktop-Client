package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MainWindowController {

  @FXML
  private Tab customersTab;

  @Autowired
  private final CustomerTableView customerTableView;

  public MainWindowController(CustomerTableView customerTableView) {
    Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

    this.customerTableView = Objects.requireNonNull(customerTableView);
  }

  @FXML
  public void initialize() {
    customersTab.setContent(customerTableView);
  }

  private void handleUncaughtException(Thread thread, Throwable throwable) {
    Platform.runLater(
        () -> Dialogs.showErrorMessage("Chyba", throwable.getMessage()));
  }
}
