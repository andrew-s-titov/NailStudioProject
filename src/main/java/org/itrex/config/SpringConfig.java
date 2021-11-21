package org.itrex.config;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@org.springframework.context.annotation.Configuration
@EnableAspectJAutoProxy
@PropertySource(value = "application.properties")
@ComponentScan("org.itrex")
public class SpringConfig implements InitializingBean {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    // TODO: create POJO with datasource property mapping
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.flyway.locations}")
    private String migrationScriptLocation;
    
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, userName, password)
                .locations(migrationScriptLocation)
                .load();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration()
                .configure()
                .addProperties(hibernateProperties())
                .buildSessionFactory();
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.driver_class", driverClass);
        properties.setProperty("hibernate.connection.username", userName);
        properties.setProperty("hibernate.connection.password", password);
        return properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        if (activeProfile.equals("prod")) {
            rootLogger.setLevel(Level.ERROR);
        } else if (activeProfile.equals("dev")) {
            rootLogger.setLevel(Level.DEBUG);
        }
        // INFO level by default in log4j.properties
    }
}