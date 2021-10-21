package org.itrex;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itrex.migrationService.FlywayService;
import org.itrex.util.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


public class RepositoryTestBaseHibernate {
    private final FlywayService flywayService = new FlywayService();
    private final static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final Session session;

    public RepositoryTestBaseHibernate() {
        session = sessionFactory.openSession();
    }

    @BeforeEach
    public void initDB() {
        flywayService.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flywayService.clean();
    }

    public Session getSession() {
        return session;
    }
}
