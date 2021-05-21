package com.ronja.crm.ronjaclient.desktop.component;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class CustomerDetailView extends VBox {

  @Autowired
  private final CustomerApiClient customerApiClient;

  private final Label label;

  public CustomerDetailView(CustomerApiClient customerApiClient) {
    this.customerApiClient = customerApiClient;

    label = new Label();
    getChildren().add(label);
    initialize();
  }

  private void initialize() {
    String text = Arrays.stream(customerApiClient.fetchAllCustomers())
        .sorted(Comparator.comparingInt(Customer::getId))
        .map(Customer::toString)
        .collect(Collectors.joining(System.lineSeparator()));
    label.setText(text);
  }
}
