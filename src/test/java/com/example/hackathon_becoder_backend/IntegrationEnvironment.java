package com.example.hackathon_becoder_backend;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;

@Testcontainers
@SpringBootTest(classes = HackathonBecoderBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationEnvironment {
    private static final Path MIGRATION_PATH = new File(".").toPath().toAbsolutePath().getParent().resolve("src/main/resources/db/changelog");
    private static final Integer EXTERNAL_PORT = 5436;
    protected static final PostgreSQLContainer<?> DB_CONTAINER;

    static {
        DB_CONTAINER = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("hackathon_becoder")
                .withUsername("postgres")
                .withPassword("postgres")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(EXTERNAL_PORT), new ExposedPort(5432)))
                ));

        DB_CONTAINER.start();
        runMigrations();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasourse.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasourse.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasourse.password", DB_CONTAINER::getPassword);
    }

    @SneakyThrows
    private static void runMigrations() {
        try (Connection conn = DB_CONTAINER.createConnection("")) {
            PostgresDatabase database = new PostgresDatabase();
            database.setConnection(new JdbcConnection(conn));

            System.out.println(MIGRATION_PATH);
            ResourceAccessor changelogDir = new DirectoryResourceAccessor(MIGRATION_PATH);
            Liquibase liquibase = new Liquibase("db.changelog-master.yaml", changelogDir, database);
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
