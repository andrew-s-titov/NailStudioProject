package org.itrex.config;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@org.springframework.context.annotation.Configuration
@EnableAspectJAutoProxy
@PropertySource(value = "application.properties")
@ComponentScan("org.itrex")
public class SpringConfig {

    @Value("${db.url}")
    private String url;
    @Value("${db.user}")
    private String user;
    @Value("${db.password}")
    private String password;
    @Value("${migration.flyway.script-location}")
    private String migrationScriptLocation;
    
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations(migrationScriptLocation)
                .load();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }
}