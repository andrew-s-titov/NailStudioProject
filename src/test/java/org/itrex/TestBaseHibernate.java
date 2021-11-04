package org.itrex;

import org.hibernate.Session;
import org.itrex.config.SpringConfig;
import org.itrex.migrationService.FlywayService;
import org.itrex.util.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TestBaseHibernate {
    private final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    private final FlywayService flywayService = context.getBean(FlywayService.class);
    private static Session session;

    public TestBaseHibernate() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    @BeforeEach
    public void initDB() {
        flywayService.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flywayService.clean();
    }

    @AfterAll
    public static void closeSession() {
        session.close();
    }

    public Session getSession() {
        return session;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
