package com.ronja.crm.ronjaclient.desktop.component.customer;

import javafx.scene.control.SplitPane;
import org.springframework.stereotype.Component;

@Component
public class CustomerSplitPane extends SplitPane {

  private CustomerDetailView customerDetailView;

  public CustomerSplitPane(CustomerTableView customerTableView) {
    customerTableView.selectedCustomer().addListener((ov, oldRemark, newRemark) -> showCustomerDetail(newRemark));
    getItems().addAll(customerTableView);
  }

  private void showCustomerDetail(CustomerTableItem customer) {
    if (customerDetailView == null) {
      customerDetailView = new CustomerDetailView();
      this.getItems().add(customerDetailView);
    }

    if (customer != null) {
      customerDetailView.setUpContent(customer);
    }
  }
}
