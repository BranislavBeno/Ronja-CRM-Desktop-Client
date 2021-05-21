package com.ronja.crm.ronjaclient.desktop.component;

import javafx.scene.control.SplitPane;
import org.springframework.stereotype.Component;

@Component
public class CustomerSplitPane extends SplitPane {

  public CustomerSplitPane(CustomerDetailView customerDetailView,
                           CustomerTableView customerTableView) {

    getItems().addAll(customerTableView, customerDetailView);
  }
}
