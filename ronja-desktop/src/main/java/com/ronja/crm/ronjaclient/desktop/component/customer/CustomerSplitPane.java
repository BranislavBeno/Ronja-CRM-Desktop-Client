package com.ronja.crm.ronjaclient.desktop.component.customer;

import javafx.scene.control.SplitPane;
import org.springframework.stereotype.Component;

@Component
public class CustomerSplitPane extends SplitPane {

    public CustomerSplitPane(CustomerTableView customerTableView) {
        CustomerDetailView customerDetailView = new CustomerDetailView();

        getItems().addAll(customerTableView, customerDetailView);
    }
}
