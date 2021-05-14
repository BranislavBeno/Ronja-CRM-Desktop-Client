package com.ronja.crm.ronjaclient.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import org.springframework.stereotype.Component;

@Component
public class MainController {

  @FXML
  public LineChart<String, Double> main;
}
