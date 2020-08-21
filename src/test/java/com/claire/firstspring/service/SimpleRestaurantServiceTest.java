package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.repository.SimpleMenuRepository;
import com.claire.firstspring.repository.SimpleRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleRestaurantServiceTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleRestaurantRepository simpleRestaurantRepository;
    SimpleMenuRepository simpleMenuRepository;
    SimpleMenuService simpleMenuService;

    @Test
    void can_get_a_list_of_restaurants() {
        SimpleRestaurantService simpleRestaurantService = new SimpleRestaurantService(
            simpleRestaurantRepository,
            simpleMenuRepository,
            simpleMenuService
        );
        assertThat(simpleRestaurantService.list())
            .hasSize(2);
    }
}