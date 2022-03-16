package com.ronja.crm.ronjaclient.service.configuration;

import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public CustomerWebClient customerWebClient(@Value("${client.customers.base-url}") String baseUrl) {
        return new CustomerWebClient(baseUrl);
    }

    @Bean
    public RepresentativeWebClient representativeWebClient(@Value("${client.representatives.base-url}") String baseUrl) {
        return new RepresentativeWebClient(baseUrl);
    }

    @Bean
    public RepresentativeMapper representativeMapper() {
        return new RepresentativeMapper();
    }

    @Bean
    public MetalDataWebClient metalDataWebClient(@Value("${client.metals.base-url}") String baseUrl) {
        return new MetalDataWebClient(baseUrl);
    }
}
