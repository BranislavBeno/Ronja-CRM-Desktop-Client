package com.ronja.crm.ronjaclient.desktop.component.configuration;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DesktopConfiguration {

  @Bean
  @Primary
  public RepresentativeTableView representativeTabView(@Autowired CustomerWebClient customerWebClient,
                                                       @Autowired RepresentativeWebClient representativeWebClient,
                                                       @Autowired RepresentativeMapper mapper) {
    return new RepresentativeTableView(customerWebClient, representativeWebClient, mapper);
  }

  @Bean(value = "Dialog")
  public RepresentativeTableView representativeDialogView(@Autowired CustomerWebClient customerWebClient,
                                                          @Autowired RepresentativeWebClient representativeWebClient,
                                                          @Autowired RepresentativeMapper mapper) {
    return new RepresentativeTableView(customerWebClient, representativeWebClient, mapper, true);
  }

  @Bean
  public CustomerTableView customerTabView(@Autowired CustomerWebClient customerWebClient,
                                           @Autowired @Qualifier("Dialog") RepresentativeTableView representativeTableView) {
    return new CustomerTableView(customerWebClient, representativeTableView);
  }
}
