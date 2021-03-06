package com.ronja.crm.ronjaclient.desktop.component.configuration;

import com.ronja.crm.ronjaclient.desktop.component.common.AppInfo;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.dashboard.DashboardPane;
import com.ronja.crm.ronjaclient.desktop.component.dashboard.MetalPane;
import com.ronja.crm.ronjaclient.desktop.component.dashboard.ScheduledPane;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import com.ronja.crm.ronjaclient.service.service.MetalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;

@Configuration
@PropertySource("classpath:git.properties")
public class DesktopConfiguration {

    @Bean
    @Primary
    public RepresentativeTableView representativeTabView(@Autowired CustomerWebClient customerWebClient,
                                                         @Autowired RepresentativeWebClient representativeWebClient,
                                                         @Autowired RepresentativeMapper mapper,
                                                         @Autowired AppInfo appInfo) {
        return new RepresentativeTableView(customerWebClient, representativeWebClient, mapper, appInfo);
    }

    @Bean(value = "Dialog")
    public RepresentativeTableView representativeDialogView(@Autowired CustomerWebClient customerWebClient,
                                                            @Autowired RepresentativeWebClient representativeWebClient,
                                                            @Autowired RepresentativeMapper mapper,
                                                            @Autowired AppInfo appInfo) {
        return new RepresentativeTableView(customerWebClient, representativeWebClient, mapper, appInfo, true);
    }

    @Bean
    public CustomerTableView customerTabView(@Autowired CustomerWebClient customerWebClient,
                                             @Autowired @Qualifier("Dialog") RepresentativeTableView representativeTableView,
                                             @Autowired AppInfo appInfo) {
        return new CustomerTableView(customerWebClient, representativeTableView, appInfo);
    }

    @Bean
    public AppInfo appInfo(@Value("${spring.application.ui.version}") String version,
                           @Value("${git.build.time}") String buildDate,
                           @Value("${git.commit.id.abbrev}") String commitId,
                           @Value("${spring.application.ui.title}") String appTitle) {
        LocalDate date = LocalDate.parse(buildDate.substring(0, buildDate.lastIndexOf('T')));
        return new AppInfo(version, date, commitId, appTitle);
    }

    @Bean
    public DashboardPane dashboardPane(@Autowired ScheduledPane scheduledPane,
                                       @Autowired MetalPane metalPane,
                                       @Autowired AppInfo appInfo) {
        return new DashboardPane(scheduledPane, metalPane, appInfo);
    }

    @Bean
    public ScheduledPane scheduledPane(@Autowired RepresentativeWebClient representativeWebClient) {
        return new ScheduledPane(representativeWebClient);
    }

    @Bean
    public MetalPane metalPane(@Autowired MetalDataService dataService) {
        return new MetalPane(dataService);
    }
}
