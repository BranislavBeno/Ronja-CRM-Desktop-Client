package com.ronja.crm.ronjaclient.desktop.component.customer;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import javafx.scene.control.SplitPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerSplitPane extends SplitPane {

    @Autowired
    private final CustomerApiClient customerApiClient;

    private final CustomerTableView customerTableView;
    private CustomerDetailView customerDetailView;

    public CustomerSplitPane(CustomerApiClient customerApiClient) {
        this.customerApiClient = Objects.requireNonNull(customerApiClient);
        customerTableView = new CustomerTableView(customerApiClient);
        customerTableView.selectedCustomer().addListener((ov, oldRemark, newRemark) -> showCustomerDetail(newRemark));
        getItems().addAll(customerTableView);
    }

    private void showCustomerDetail(CustomerTableItem customer) {
        if (customerDetailView == null) {
            customerDetailView = new CustomerDetailView(customerApiClient, customerTableView);
            this.getItems().add(customerDetailView);
        }

        if (customer != null) {
            customerDetailView.setUpContent(customer);
        }
    }
}
