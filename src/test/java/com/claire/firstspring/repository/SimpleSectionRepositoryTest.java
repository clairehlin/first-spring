package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleSectionRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

}