package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Restaurant;
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
        classes = {SimpleRestaurantRepositoryTest.TestDataSource.class}
)
@TestPropertySource(
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class SimpleRestaurantRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleRestaurantRepository simpleRestaurantRepository;

    @Test
    void can_read_restaurants_from_repo() {
        assertThat(simpleRestaurantRepository.restaurants())
                .hasSize(2)
                .isNotEmpty()
                .extracting(Restaurant::name)
                .containsExactlyInAnyOrder("Ruth Steakhouse", "Sam Steakhouse");
    }

    @Configuration
    @Import({PersistenceConfig.class, SimpleRestaurantRepository.class})
    static class TestDataSource {
        @Bean
        @Primary
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .generateUniqueName(true)
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:schema.sql")
                    .addScript("classpath:test-data.sql")
                    .build();
        }
    }

}