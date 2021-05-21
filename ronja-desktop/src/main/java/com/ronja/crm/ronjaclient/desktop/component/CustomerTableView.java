package com.ronja.crm.ronjaclient.desktop.component;

import com.ronja.crm.ronjaclient.service.communication.CustomerApiClient;
import com.ronja.crm.ronjaclient.service.domain.Customer;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

@Component
public class CustomerTableView extends TableView<Customer> {

  public CustomerTableView(CustomerApiClient customerApiClient) {
  }
}
