package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleSection;
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
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(
    classes = {SimpleMenuRepositoryTest.TestDataSource.class}
)
@TestPropertySource(
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@Sql({"classpath:db/V1__db_init.sql", "classpath:test-data.sql"})
class SimpleMenuRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleMenuRepository simpleMenuRepository;

    @Test
    void can_get_restaurant_menus() {
        List<Menu> menus = simpleMenuRepository.restaurantMenus(1);
        assertThat(menus).hasSize(2);
    }

    @Test
    void can_create_restaurant_menu(){
        //given
        Set<Section> sections = new HashSet<>();

        SimpleMenu simpleMenu = new SimpleMenu(4, "Kids Menu", sections);

        //when
        simpleMenuRepository.create(1, simpleMenu);

        //then
        List<Menu> menus = simpleMenuRepository.menus();
        assertThat(menus).hasSize(4);
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
                .build();
        }

        @Bean
        @Primary
        public String schema() {
            return "\"PUBLIC\"";
        }
    }
}