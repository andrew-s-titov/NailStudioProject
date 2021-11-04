package org.itrex.config;

import org.hibernate.SessionFactory;
import org.itrex.migrationService.FlywayService;
import org.itrex.util.HibernateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.itrex.repositories.impl")
public class SpringConfig {
    @Bean
    public SessionFactory sessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    @Bean
    public FlywayService flywayService() {
        FlywayService flywayService = new FlywayService();
        flywayService.migrate();
        return flywayService;
    }
}