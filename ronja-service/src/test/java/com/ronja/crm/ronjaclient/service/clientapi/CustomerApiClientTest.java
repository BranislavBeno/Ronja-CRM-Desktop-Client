package com.ronja.crm.ronjaclient.service.clientapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@SpringBootTest(classes = CustomerApiClient.class)
@AutoConfigureMockRestServiceServer
@Import(CustomerTestConfiguration.class)
@TestPropertySource(locations = "classpath:test.properties")

public class CustomerApiClientTest {

  @Autowired
  private MockRestServiceServer mockRestServiceServer;
  @MockBean
  private CustomerApiClient cut;
  @Autowired
  private RestTemplateBuilder restTemplateBuilder;

  @Test
  public void testSuccessfulFetching() {
    assertThat(mockRestServiceServer).isNotNull();
    assertThat(cut).isNotNull();
  }

  @Test
  public void shouldPropagateExceptionWhenRemoteSystemIsDown() {
    assertThrows(IllegalStateException.class, () -> {
      this.mockRestServiceServer
          .expect(requestTo("/list"))
          .andRespond(MockRestResponseCreators.withServerError());
      cut.fetchAllCustomers();
    });
  }
}