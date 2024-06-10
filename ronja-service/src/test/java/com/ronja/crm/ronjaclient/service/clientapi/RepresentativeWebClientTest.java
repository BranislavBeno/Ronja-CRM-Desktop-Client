package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.*;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeDto;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import com.ronja.crm.ronjaclient.service.validation.DeleteException;
import com.ronja.crm.ronjaclient.service.validation.FetchException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.WithAssertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.function.Supplier;

class RepresentativeWebClientTest implements WithAssertions {

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
                    "contactType": "MAIL",
                    "phoneNumbers": [
                        {
                            "contact": "+420920920920",
                            "type": "HOME",
                            "primary": false
                        },
                        {
                            "contact": "+420920920920",
                            "type": "HOME",
                            "primary": false
                        }
                    ],
                    "emails": [
                        {
                            "contact": "john@example.com",
                            "type": "WORK",
                            "primary": true
                        }
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
                    "contactType": "PHONE",
                    "phoneNumbers": [
                        {
                            "contact": "+420920920920",
                            "type": "HOME",
                            "primary": false
                        }
                    ],
                    "emails": [
                        {
                            "contact": "jane@example.com",
                            "type": "WORK",
                            "primary": true
                        }
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
    private static final String ONE_ITEM_LIST_RESPONSE = """
            [
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
                    "contactType": "PHONE",
                    "phoneNumbers": [
                        {
                            "contact": "+420920920920",
                            "type": "HOME",
                            "primary": false
                        }
                    ],
                    "emails": [
                        {
                            "contact": "jane@example.com",
                            "type": "WORK",
                            "primary": true
                        }
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
                "contactType": "MAIL",
                "phoneNumbers": [],
                "emails": [],
                "customer": null
            }""";

    private MockWebServer mockWebServer;

    private RepresentativeWebClient representativeWebClient;

    private RepresentativeMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.representativeWebClient = new RepresentativeWebClient(mockWebServer.url("/").toString());
        this.mapper = new RepresentativeMapper();
    }

    @AfterEach
    void shutdown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Representative all list fetching: happy path test")
    void testRepresentativeListFetching() {
        mockResponse(LIST_RESPONSE);

        Representative[] representatives = representativeWebClient.fetchAllRepresentatives().block();
        assertThat(representatives).isNotNull().satisfies(rs -> {
            assertThat(rs).hasSize(2);

            Representative representative = rs[0];
            assertThat(representative).isNotNull().satisfies(r ->
                    Assertions.assertAll(() -> {
                        assertThat(r.getId()).isEqualTo(1);
                        assertThat(r.getFirstName()).isEqualTo("John");
                        assertThat(r.getLastName()).isEqualTo("Doe");
                        assertThat(r.getPosition()).isEqualTo("CEO");
                        assertThat(r.getRegion()).isEqualTo("V4");
                        assertThat(r.getNotice()).isEqualTo("nothing special");
                        assertThat(r.getStatus()).isEqualTo(Status.ACTIVE);
                        assertThat(r.getLastVisit()).isEqualTo(LocalDate.of(2020, 10, 7));
                        assertThat(r.getScheduledVisit()).isEqualTo(LocalDate.of(2021, 4, 25));
                        assertThat(r.getPhoneNumbers()).hasSize(2);
                        Contact expected = new Contact("john@example.com", "HOME", true);
                        Contact actual = r.getEmails().stream().findFirst().orElseThrow();
                        assertThat(expected.getContent()).isEqualTo(actual.getContent());
                        assertThat(r.getCustomer().getCompanyName()).isEqualTo("LeslieCorp");
                        assertThat(r.getCustomer().getCategory()).isEqualTo(Category.LEVEL_1);
                        assertThat(r.getCustomer().getFocus()).isEqualTo(Focus.BUILDER);
                        assertThat(r.getCustomer().getStatus()).isEqualTo(Status.ACTIVE);
                    })
            );
        });
    }

    @Test
    @DisplayName("Representative particular list fetching: happy path test")
    void testRepresentativeParticularListFetching() {
        mockResponse(ONE_ITEM_LIST_RESPONSE);

        Representative[] representatives = representativeWebClient.fetchParticularRepresentatives(2).block();
        Assertions.assertAll(() -> {
            assertThat(representatives).isNotNull();
            assertThat(representatives).hasSize(1);
        });
    }

    @Test
    @DisplayName("Representative creating: happy path test")
    void testRepresentativeCreating() {
        mockResponse(SINGLE_RESPONSE);

        Representative representative = provideRepresentative();
        RepresentativeDto dto = mapper.toDto(representative);
        Representative newRepresentative = representativeWebClient.createRepresentative(dto).block();

        assertThat(newRepresentative).isNotNull().satisfies(nr -> assertThat(nr.getLastName()).isEqualTo(representative.getLastName()));
    }

    @Test
    @DisplayName("Representative updating: happy path test")
    void testRepresentativeUpdating() {
        mockResponse(SINGLE_RESPONSE);
        Representative representative = provideNewRepresentative();
        RepresentativeDto dto = mapper.toDto(representative);
        Representative updatedRepresentative = representativeWebClient.updateRepresentative(dto).block();
        assertThat(updatedRepresentative).isNotNull();
    }

    @Test
    @DisplayName("Representative deleting: happy path test")
    void testRepresentativeDeleting() {
        mockResponse();
        assertThatCode(() -> representativeWebClient.deleteRepresentative(1).block()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Representative data handling: failure test")
    void testExceptionsOnRepresentativeDataHandling() {
        RepresentativeDto dto = mapper.toDto(provideNewRepresentative());
        Assertions.assertAll(() -> {
            // representative list fetching failures
            assertThatThrownBy(this::fetchRepresentatives).isExactlyInstanceOf(FetchException.class);
            // representative creating failures
            assertThatThrownBy(() -> propagateExceptionWith400ServerError(
                    () -> representativeWebClient.createRepresentative(dto).block()))
                    .isExactlyInstanceOf(ServerErrorException.class);
            // representative updating failures
            assertThatThrownBy(() -> propagateExceptionWith400ServerError(
                    () -> representativeWebClient.updateRepresentative(dto).block()))
                    .isExactlyInstanceOf(ServerErrorException.class);
            // representative deleting failures
            assertThatThrownBy(this::deleteRepresentative).isExactlyInstanceOf(DeleteException.class);
        });
    }

    private void propagateExceptionWith400ServerError(Supplier<Representative> supplier) {
        provideResponse();
        supplier.get();
    }

    private void fetchRepresentatives() {
        provideResponse();
        representativeWebClient.fetchAllRepresentatives().block();
    }

    private void deleteRepresentative() {
        provideResponse();
        representativeWebClient.deleteRepresentative(0).block();
    }

    private void provideResponse() {
        this.mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Error occurred."));
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
        representative.setContactType(ContactType.PHONE);
        representative.setPhoneNumbers(Collections.emptyList());
        representative.setEmails(Collections.emptyList());
        representative.setCustomer(null);
        return representative;
    }

    @NotNull
    private Representative provideNewRepresentative() {
        Representative representative = new Representative();
        representative.setStatus(Status.ACTIVE);
        representative.setPhoneNumbers(Collections.emptyList());
        representative.setEmails(Collections.emptyList());
        return representative;
    }
}