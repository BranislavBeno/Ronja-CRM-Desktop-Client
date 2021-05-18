package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import org.springframework.stereotype.Component;

@Component
public class MainController {

  @FXML
  public LineChart<String, Double> main;

  private final CustomerApiClient customerApiClient;

  public MainController(CustomerApiClient customerApiClient) {
    this.customerApiClient = customerApiClient;
  }
}
