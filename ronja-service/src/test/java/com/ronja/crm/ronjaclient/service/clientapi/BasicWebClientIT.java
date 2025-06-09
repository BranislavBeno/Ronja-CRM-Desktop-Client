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
    static final MySQLContainer<?> RONJA_DB = populateDatabase();

    @Container
    static final GenericContainer<?> RONJA_SERVER = populateServer();

    static {
        Startables.deepStart(RONJA_DB, RONJA_SERVER).join();
    }

    private static MySQLContainer<?> populateDatabase() {
        try (MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse("mysql:lts"))) {
            return db.withExposedPorts(3306)
                    .withAccessToHost(true)
                    .withDatabaseName("ronja")
                    .withUsername("ronja")
                    .withPassword("ronja")
                    .withNetwork(NETWORK)
                    .withNetworkAliases("ronja_db");
        }
    }

    private static GenericContainer<?> populateServer() {
        try (GenericContainer<?> server = new GenericContainer<>(DockerImageName.parse("beo1975/ronja-server:1.3.3"))) {
            return server.withExposedPorts(8087)
                    .waitingFor(Wait.forHttp("/actuator/health"))
                    .withNetwork(NETWORK)
                    .withEnv("SPRING_DATASOURCE_URL", "jdbc:mysql://ronja_db:3306/ronja?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC")
                    .withEnv("SPRING_DATASOURCE_USERNAME", "ronja")
                    .withEnv("SPRING_DATASOURCE_PASSWORD", "ronja")
                    .dependsOn(RONJA_DB);
        }
    }
}
