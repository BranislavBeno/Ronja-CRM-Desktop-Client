package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.CustomerSplitPane;
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
    this.customerSplitPane = customerSplitPane;
  }

  @FXML
  public void initialize() {
    customersTab.setContent(customerSplitPane);
  }
}
