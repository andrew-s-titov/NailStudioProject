package org.itrex;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.itrex.dbProperties.H2Properties.*;

public class RepositoryTestBaseJDBC {
    private final Flyway flyway;
    private final JdbcConnectionPool connectionPool;

    public RepositoryTestBaseJDBC(@Autowired Flyway flyway) {
        this.flyway = flyway;
        connectionPool = JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);
    }

    @BeforeEach
    public void initDB() {
        flyway.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flyway.clean();
    }

    public JdbcConnectionPool getConnectionPool() {
        return connectionPool;
    }
}