package org.itrex;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManagerFactory;

@SpringBootTest
public class TestBaseHibernate {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Flyway flyway;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
        return entityManagerFactory.unwrap(SessionFactory.class);
    }
}