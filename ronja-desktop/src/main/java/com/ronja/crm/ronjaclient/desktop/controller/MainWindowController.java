package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
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
  @FXML
  private Tab representativesTab;

  @Autowired
  private final CustomerTableView customerTableView;
  @Autowired
  private final RepresentativeTableView representativeTableView;

  public MainWindowController(CustomerTableView customerTableView,
                              RepresentativeTableView representativeTableView) {
    Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

    this.customerTableView = Objects.requireNonNull(customerTableView);
    this.representativeTableView = Objects.requireNonNull(representativeTableView);
  }

  @FXML
  public void initialize() {
    customersTab.setContent(customerTableView);
    representativesTab.setContent(representativeTableView);
  }

  private void handleUncaughtException(Thread thread, Throwable throwable) {
    Platform.runLater(
        () -> Dialogs.showErrorMessage("Chyba", throwable.getMessage()));
  }
}
