package com.ronja.crm.ronjaclient.desktop.component.customer;

import javafx.scene.control.SplitPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerSplitPane extends SplitPane {

  public CustomerSplitPane(@Autowired CustomerTableView customerTableView) {
    Objects.requireNonNull(customerTableView);

    var customerDetailView = new CustomerDetailView();
    getItems().addAll(customerTableView, customerDetailView);
  }
}
