package org.itrex;

import org.h2.jdbcx.JdbcConnectionPool;
import org.itrex.migrationService.FlywayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.itrex.dbProperties.H2Properties.*;

public class RepositoryTestBase {
    private final FlywayService flywayService;
    private final JdbcConnectionPool connectionPool;

    public RepositoryTestBase() {
        flywayService = new FlywayService();
        connectionPool = JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);
    }

    @BeforeEach
    public void initDB() {
        flywayService.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flywayService.clean();
    }

    public JdbcConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
