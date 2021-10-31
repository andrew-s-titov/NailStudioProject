package org.itrex;

import org.hibernate.Session;
import org.itrex.migrationService.FlywayService;
import org.itrex.util.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


public class TestBaseHibernate {
    private final FlywayService flywayService = new FlywayService();
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
}