package com.ronja.crm.ronjaclient.service.clientapi;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
abstract class BasicWebClientIT {

    private static final Network NETWORK = Network.newNetwork();

    @Container
    static final MySQLContainer<?> RONJA_DB = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.25"))
            .withExposedPorts(3306)
            .withAccessToHost(true)
            .withDatabaseName("ronja")
            .withUsername("ronja")
            .withPassword("ronja")
            .withNetwork(NETWORK)
            .withNetworkAliases("ronja_db");

    @Container
    static final GenericContainer<?> RONJA_SERVER = new GenericContainer<>(DockerImageName.parse("beo1975/ronja-server:1.2.0"))
            .withExposedPorts(8087)
            .waitingFor(Wait.forHttp("/actuator/health"))
            .withNetwork(NETWORK)
            .withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://ronja_db:3306/ronja?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC")
            .withEnv("SPRING_DATASOURCE_USERNAME", "ronja")
            .withEnv("SPRING_DATASOURCE_PASSWORD", "ronja")
            .dependsOn(RONJA_DB);

    static {
        Startables.deepStart(RONJA_DB, RONJA_SERVER).join();
    }
}
