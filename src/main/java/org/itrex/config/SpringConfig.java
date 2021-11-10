package org.itrex.config;

import static org.itrex.dbProperties.H2Properties.*;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan({"org.itrex.repositories.impl", "org.itrex.services"})
public class SpringConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(DB_URL, DB_USER, DB_PASSWORD)
                .locations(SCRIPT_LOCATION)
                .load();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }
}