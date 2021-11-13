package org.itrex;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.itrex.config.SpringConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestBaseHibernate {
    private final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    private final Flyway flyway = context.getBean(Flyway.class);
    private final SessionFactory sessionFactory = context.getBean(SessionFactory.class);

    @BeforeEach
    public void initDB() {
        flyway.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flyway.clean();
    }

    public ApplicationContext getContext() {
        return context;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}