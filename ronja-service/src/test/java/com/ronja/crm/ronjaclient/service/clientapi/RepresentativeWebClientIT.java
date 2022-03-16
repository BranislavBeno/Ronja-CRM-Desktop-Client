package com.ronja.crm.ronjaclient.service.clientapi;

import com.ronja.crm.ronjaclient.service.domain.*;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeDto;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepresentativeWebClientIT extends BasicWebClientIT {

    private static final int PLUS_DAYS = 14;
    private CustomerWebClient customerWebClient;
    private RepresentativeWebClient representativeWebClient;
    private final RepresentativeMapper mapper = new RepresentativeMapper();

    @BeforeEach
    void setUp() {
        String url = "http://%s:%d/customers".formatted(RONJA_SERVER.getHost(), RONJA_SERVER.getMappedPort(8087));
        customerWebClient = new CustomerWebClient(url);

        url = "http://%s:%d/representatives".formatted(RONJA_SERVER.getHost(), RONJA_SERVER.getMappedPort(8087));
        representativeWebClient = new RepresentativeWebClient(url);
    }

    @Test
    @Order(1)
    @DisplayName("Test-containers: Test whether new representative is created successfully")
    void testAddNewRepresentative() {
        Representative representative = createRepresentative().block();

        assertThat(representative).isNotNull();
        assertThat(representative.getId()).isNotZero();
    }

    @Test
    @Order(2)
    @DisplayName("Test-containers: Test whether all representatives are fetched successfully")
    void testFetchRepresentatives() {
        Representative[] representatives = representativeWebClient.fetchAllRepresentatives().block();
        assertThat(representatives).isNotEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Test-containers: Test whether particular representatives are fetched successfully")
    void testFetchParticularRepresentatives() {
        Representative[] representatives = representativeWebClient.fetchParticularRepresentatives(1).block();
        assertThat(representatives).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Test-containers: Test whether existing representative is updated successfully")
    void testUpdateRepresentative() {
        Representative[] representatives = representativeWebClient.fetchAllRepresentatives().block();
        Representative representative = Objects.requireNonNull(representatives)[0];
        assertThat(representative.getStatus()).isEqualTo(Status.INACTIVE);

        representative.setStatus(Status.ACTIVE);
        RepresentativeDto dto = mapper.toDto(representative);
        Representative updatedRepresentative = representativeWebClient.updateRepresentative(dto).block();
        assertThat(Objects.requireNonNull(updatedRepresentative).getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @Order(5)
    @DisplayName("Test-containers: Test whether representative is deleted successfully")
    void testDeleteRepresentative() {
        Representative[] representatives = representativeWebClient.fetchAllRepresentatives().block();
        assertThat(representatives).hasSize(1);

        representativeWebClient.deleteRepresentative(1).block();
        representatives = representativeWebClient.fetchAllRepresentatives().block();
        assertThat(representatives).isEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("Test-containers: Test whether representatives scheduled for next 14 days are fetched successfully")
    void testFetchScheduledRepresentatives() {
        Representative[] representatives = representativeWebClient.fetchScheduledRepresentatives(PLUS_DAYS).block();
        assertThat(representatives).isEmpty();

        createRepresentative().block();
        representatives = representativeWebClient.fetchScheduledRepresentatives(PLUS_DAYS).block();
        assertThat(representatives).hasSize(1);
    }

    private Mono<Representative> createRepresentative() {
        Representative representative = new Representative();
        representative.setFirstName("Joe");
        representative.setLastName("Doe");
        representative.setPosition("");
        representative.setRegion("");
        representative.setNotice("");
        representative.setStatus(Status.INACTIVE);
        representative.setContactType(ContactType.PHONE);
        representative.setPhoneNumbers(Collections.emptyList());
        representative.setEmails(Collections.emptyList());
        representative.setLastVisit(LocalDate.now().minusDays(1));
        representative.setScheduledVisit(LocalDate.now().plusDays(1));
        representative.setCustomer(provideCustomer().block());

        RepresentativeDto dto = mapper.toDto(representative);
        return representativeWebClient.createRepresentative(dto);
    }

    private Mono<Customer> provideCustomer() {
        Customer customer = new Customer();
        customer.setCompanyName("test");
        customer.setCategory(Category.LEVEL_1);
        customer.setFocus(Focus.BUILDER);
        customer.setStatus(Status.ACTIVE);

        return Objects.requireNonNull(customerWebClient.createCustomer(customer));
    }
}
