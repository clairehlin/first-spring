package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.repository.FeatureRowMapper;
import com.claire.firstspring.repository.ItemRowMapper;
import com.claire.firstspring.repository.MenuRowMapper;
import com.claire.firstspring.repository.RestaurantRowMapper;
import com.claire.firstspring.repository.SectionRowMapper;
import com.claire.firstspring.repository.SimpleFeatureRepository;
import com.claire.firstspring.repository.SimpleIdGeneratingRepository;
import com.claire.firstspring.repository.SimpleItemRepository;
import com.claire.firstspring.repository.SimpleMenuRepository;
import com.claire.firstspring.repository.SimpleRestaurantRepository;
import com.claire.firstspring.repository.SimpleSectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(
    classes = {SimpleRestaurantServiceTest.TestDataSource.class}
)
@TestPropertySource(
    properties = "spring.main.allow-bean-definition-overriding=true"
)
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

    @Configuration
    @Import({
        PersistenceConfig.class,
        SimpleRestaurantRepository.class,
        RestaurantRowMapper.class,
        SimpleMenuRepository.class,
        MenuRowMapper.class,
        SimpleSectionRepository.class,
        SectionRowMapper.class,
        SimpleItemRepository.class,
        SimpleFeatureRepository.class,
        ItemRowMapper.class,
        FeatureRowMapper.class,
        SimpleIdGeneratingRepository.class
    })
    static class TestDataSource {
        @Bean
        @Primary
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:db/V1__db_init.sql")
                .addScript("classpath:test-data.sql")
                .build();
        }

        @Bean
        @Primary
        public String schema() {
            return "\"PUBLIC\"";
        }
    }
}