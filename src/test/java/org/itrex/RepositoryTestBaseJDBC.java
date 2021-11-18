package org.itrex;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryTestBaseJDBC {
    private final Flyway flyway;
    private final JdbcConnectionPool connectionPool;
    public final String DB_URL = "jdbc:h2:mem:PUBLIC;DB_CLOSE_DELAY=-1";
    public final String DB_USER = "admin";
    public final String DB_PASSWORD = "1234";

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