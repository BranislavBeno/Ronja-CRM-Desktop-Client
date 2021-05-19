package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class MainController {

  @FXML
  private Label label;

  @Autowired
  private final CustomerApiClient customerApiClient;

  public MainController(CustomerApiClient customerApiClient) {
    this.customerApiClient = customerApiClient;
  }

  @FXML
  public void initialize() {
    String text = Arrays.stream(customerApiClient.fetchAllCustomers())
        .sorted(Comparator.comparingInt(Customer::getId))
        .map(Customer::toString)
        .collect(Collectors.joining(System.lineSeparator()));
    label.setText(text);
  }
}
