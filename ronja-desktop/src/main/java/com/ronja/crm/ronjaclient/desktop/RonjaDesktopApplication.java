package com.ronja.crm.ronjaclient.desktop;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RonjaDesktopApplication {

  public static void main(String[] args) {
    Application.launch(DesktopApplication.class, args);
  }
}
