package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.configuration.CustomerClientConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RestClientTest(CustomerApiClient.class)
@ContextConfiguration(classes = CustomerClientConfiguration.class)
public class CustomerApiClientTest {

  @Autowired
  private MockRestServiceServer mockRestServiceServer;
  @Autowired
  private CustomerApiClient customerApiClient;

  @Test
  public void testSuccessfulFetching() {
    assertThat(mockRestServiceServer).isNotNull();
    assertThat(customerApiClient).isNotNull();
  }

  @Test
  public void shouldPropagateExceptionWhenRemoteSystemIsDown() {
    assertThrows(IllegalStateException.class, () -> {
      this.mockRestServiceServer
          .expect(requestTo("/list"))
          .andRespond(MockRestResponseCreators.withServerError());
      customerApiClient.fetchAllCustomers();
    });
  }
}