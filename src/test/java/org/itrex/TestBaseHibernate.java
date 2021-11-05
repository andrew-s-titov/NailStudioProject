package org.itrex;

import org.itrex.config.SpringConfig;
import org.itrex.migrationService.FlywayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TestBaseHibernate {
    private final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    private final FlywayService flywayService = context.getBean(FlywayService.class);

    @BeforeEach
    public void initDB() {
        flywayService.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flywayService.clean();
    }

    public ApplicationContext getContext() {
        return context;
    }
}
