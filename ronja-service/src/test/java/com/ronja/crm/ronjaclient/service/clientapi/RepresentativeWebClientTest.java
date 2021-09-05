package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.Category;
import com.ronja.crm.ronjaclient.service.domain.Focus;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import com.ronja.crm.ronjaclient.service.domain.Status;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RepresentativeWebClientTest {

  private static final String LIST_RESPONSE = """
      [
          {
              "id": 1,
              "firstName": "John",
              "lastName": "Doe",
              "position": "CEO",
              "region": "V4",
              "notice": "nothing special",
              "status": "ACTIVE",
              "lastVisit": "2020-10-07",
              "scheduledVisit": "2021-04-25",
              "phoneNumbers": [
                  "+420920920920",
                  "+421987654321"
              ],
              "emails": [
                  "john@example.com",
                  "doe@foo.com"
              ],
              "customer": {
                  "id": 1,
                  "companyName": "LeslieCorp",
                  "category": "LEVEL_1",
                  "focus": "BUILDER",
                  "status": "ACTIVE"
              }
          },
          {
              "id": 2,
              "firstName": "Jane",
              "lastName": "Smith",
              "position": "CFO",
              "region": "EMEA",
              "notice": "anything special",
              "status": "INACTIVE",
              "lastVisit": "2020-10-07",
              "scheduledVisit": "2021-04-25",
              "phoneNumbers": [
                  "+420920920920"
              ],
              "emails": [
                  "jane@example.com"
              ],
              "customer": {
                  "id": 2,
                  "companyName": "EmmaCorp",
                  "category": "LEVEL_2",
                  "focus": "MANUFACTURE",
                  "status": "INACTIVE"
              }
          }
      ]""";

  private static final String SINGLE_RESPONSE = """
      {
          "id": 8,
          "firstName": "Henry",
          "lastName": "Tudor",
          "position": "CTO",
          "region": "EMEA",
          "notice": "",
          "status": "INACTIVE",
          "lastVisit": "2021-09-03",
          "scheduledVisit": "2021-09-03",
          "phoneNumbers": [],
          "emails": [],
          "customer": null
      }""";

  private MockWebServer mockWebServer;

  private RepresentativeWebClient representativeWebClient;

  @BeforeEach
  public void setUp() throws IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    this.representativeWebClient = new RepresentativeWebClient(mockWebServer.url("/").toString());
  }

  @AfterEach
  public void shutdown() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  @DisplayName("Representative list fetching: happy path test")
  public void testRepresentativeListFetching() {
    mockResponse(LIST_RESPONSE);

    Representative[] representatives = representativeWebClient.fetchAllRepresentatives().block();
    assertAll(() -> {
      assertThat(representatives).isNotNull();
      assertThat(representatives).hasSize(2);
    });

    Representative representative = representatives[0];
    assertAll(() -> {
      assertThat(representative.getId()).isEqualTo(1);
      assertThat(representative.getFirstName()).isEqualTo("John");
      assertThat(representative.getLastName()).isEqualTo("Doe");
      assertThat(representative.getPosition()).isEqualTo("CEO");
      assertThat(representative.getRegion()).isEqualTo("V4");
      assertThat(representative.getNotice()).isEqualTo("nothing special");
      assertThat(representative.getStatus()).isEqualTo(Status.ACTIVE);
      assertThat(representative.getLastVisit()).isEqualTo(LocalDate.of(2020, 10, 7));
      assertThat(representative.getScheduledVisit()).isEqualTo(LocalDate.of(2021, 4, 25));
      assertThat(representative.getPhoneNumbers()).hasSize(2);
      assertThat(representative.getEmails().stream().findFirst()).isEqualTo(Optional.of("john@example.com"));
      assertThat(representative.getCustomer().getCompanyName()).isEqualTo("LeslieCorp");
      assertThat(representative.getCustomer().getCategory()).isEqualTo(Category.LEVEL_1);
      assertThat(representative.getCustomer().getFocus()).isEqualTo(Focus.BUILDER);
      assertThat(representative.getCustomer().getStatus()).isEqualTo(Status.ACTIVE);
    });
  }

  @Test
  @DisplayName("Representative creating: happy path test")
  public void testRepresentativeCreating() {
    mockResponse(SINGLE_RESPONSE);

    Representative representative = provideRepresentative();
    Representative newRepresentative = representativeWebClient.createRepresentative(representative).block();

    assertAll(() -> {
      assertThat(newRepresentative).isNotNull();
      assertThat(newRepresentative.getLastName()).isEqualTo(representative.getLastName());
    });
  }

  @Test
  @DisplayName("Representative updating: happy path test")
  public void testRepresentativeUpdating() {
    mockResponse(SINGLE_RESPONSE);
    Representative representative = provideNewRepresentative();
    Representative updatedRepresentative = representativeWebClient.updateRepresentative(representative).block();
    assertThat(updatedRepresentative).isNotNull();
  }

  @Test
  @DisplayName("Representative deleting: happy path test")
  public void testRepresentativeDeleting() {
    mockResponse();
    assertThatCode(() -> representativeWebClient.deleteRepresentative(1).block()).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Representative data handling: failure test")
  public void testExceptionsOnRepresentativeDataHandling() {
    assertAll(() -> {
      // representative list fetching failures
      assertThatThrownBy(() -> fetchRepresentatives(400)).isInstanceOf(RuntimeException.class);
      assertThatThrownBy(() -> fetchRepresentatives(500)).isExactlyInstanceOf(FetchException.class);
      // representative creating failures
      assertThatThrownBy(() -> propagateExceptionWith500ServerError(
          () -> representativeWebClient.createRepresentative(provideNewRepresentative()).block()))
          .isExactlyInstanceOf(SaveException.class);
      assertThatThrownBy(() -> propagateExceptionWith400ServerError(
          () -> representativeWebClient.createRepresentative(provideNewRepresentative()).block()))
          .isInstanceOf(RuntimeException.class);
      // representative updating failures
      assertThatThrownBy(() -> propagateExceptionWith500ServerError(
          () -> representativeWebClient.updateRepresentative(provideNewRepresentative()).block()))
          .isExactlyInstanceOf(SaveException.class);
      assertThatThrownBy(() -> propagateExceptionWith400ServerError(
          () -> representativeWebClient.updateRepresentative(provideNewRepresentative()).block()))
          .isInstanceOf(RuntimeException.class);
      // representative deleting failures
      assertThatThrownBy(() -> deleteRepresentative(400)).isInstanceOf(RuntimeException.class);
      assertThatThrownBy(() -> deleteRepresentative(500)).isExactlyInstanceOf(DeleteException.class);
    });
  }

  private void propagateExceptionWith400ServerError(Supplier<Representative> supplier) {
    provideResponse(400, "Error occurred.");
    supplier.get();
  }

  private void propagateExceptionWith500ServerError(Supplier<Representative> supplier) {
    provideResponse(500, "System is down.");
    supplier.get();
  }

  private void fetchRepresentatives(int i) {
    provideResponse(i, "Error occurred.");
    representativeWebClient.fetchAllRepresentatives().block();
  }

  private void deleteRepresentative(int i) {
    provideResponse(i, "Error occurred.");
    representativeWebClient.deleteRepresentative(0).block();
  }

  private void provideResponse(int i, String s) {
    this.mockWebServer.enqueue(new MockResponse()
        .setResponseCode(i)
        .setBody(s));
  }

  private void mockResponse() {
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8");
    this.mockWebServer.enqueue(mockResponse);
  }

  private void mockResponse(String listResponse) {
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody(listResponse);
    this.mockWebServer.enqueue(mockResponse);
  }

  private Representative provideRepresentative() {
    Representative representative = new Representative();
    representative.setId(8);
    representative.setFirstName("Henry");
    representative.setLastName("Tudor");
    representative.setPosition("CTO");
    representative.setRegion("EMEA");
    representative.setNotice("");
    representative.setStatus(Status.INACTIVE);
    representative.setLastVisit(LocalDate.of(2021, 9, 3));
    representative.setScheduledVisit(LocalDate.of(2021, 9, 3));
    representative.setPhoneNumbers(Collections.emptyList());
    representative.setEmails(Collections.emptyList());
    representative.setCustomer(null);
    return representative;
  }

  @NotNull
  private Representative provideNewRepresentative() {
    Representative representative = new Representative();
    representative.setPhoneNumbers(Collections.emptyList());
    representative.setEmails(Collections.emptyList());
    return representative;
  }
}