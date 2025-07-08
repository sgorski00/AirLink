package pl.sgorski.AirLink.containers_config;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {

    private static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:17.2")
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");
        POSTGRES.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return POSTGRES;
    }
}
