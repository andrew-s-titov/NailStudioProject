package org.itrex.migrationService;

import org.flywaydb.core.Flyway;

import static org.itrex.dbProperties.H2Properties.*;

public class FlywayService {

    private Flyway flyway;

    public FlywayService() {
        init();
    }

    public void migrate() {
        flyway.migrate();
    }

    public void clean() {
        flyway.clean();
    }

    private void init() {
        flyway = Flyway.configure()
                .dataSource(DB_URL, DB_USER, DB_PASSWORD)
                .locations(SCRIPT_LOCATION)
                .load();
    }

}