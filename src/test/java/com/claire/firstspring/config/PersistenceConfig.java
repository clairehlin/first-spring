package com.claire.firstspring.config;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {
    @Value("${database.driver.class.name}")
    private String driverClassName;

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.username}")
    private String databaseUsername;

    @Value("${database.password}")
    private String databasePassword;

    @Bean
    MariaDB4jSpringService mariaDB4jSpringService() {
        long pid = ProcessHandle.current().pid();
        MariaDB4jSpringService mariaDB4jSpringService = new MariaDB4jSpringService();
        mariaDB4jSpringService.setDefaultBaseDir("/tmp/MariaDB4j/base-" + pid);
        mariaDB4jSpringService.setDefaultDataDir("/tmp/MariaDB4j/data-" + pid);
        return mariaDB4jSpringService;
    }

    @Bean
    public DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService) throws ManagedProcessException {

        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();
        mariaDB4jSpringService.getDB().createDB("test");

        DataSource dataSource = DataSourceBuilder
            .create()
            .username(databaseUsername)
            .password(databasePassword)
            .url(config.getURL("test").replace("mysql", "mariadb"))
            .driverClassName(driverClassName)
            .build();

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

