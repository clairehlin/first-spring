package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleMenuRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleMenuRepository simpleMenuRepository;

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_get_restaurant_menus_with_restaurant_id() {
            // given
            List<Menu> menus = simpleMenuRepository.restaurantMenus(1);

            // when/then
            assertThat(menus).extracting(Menu::name).containsExactlyInAnyOrder("Simple Menu", "Holiday Menu");
        }

        @Test
        void can_get_a_list_of_menus() {
            // given
            final List<Menu> menus = simpleMenuRepository.menus();

            // when/then
            assertThat(menus).extracting(Menu::name).containsExactlyInAnyOrder("Simple Menu", "Holiday Menu", "Standard Menu");
        }

        @Test
        void can_get_menus_with_menu_id() {
            // given
            final Optional<Menu> menu = simpleMenuRepository.menu(1);

            // when/then
            assertThat(menu.map(Menu::name)).contains("Simple Menu");
        }

        @Test
        void can_get_menus_with_restaurant_id() {
            // given
            final List<Menu> menus = simpleMenuRepository.restaurantMenus(1);

            // when/then
            assertThat(menus.stream().map(Menu::name)).containsExactlyInAnyOrder("Simple Menu", "Holiday Menu");
        }

        @Test
        void fails_to_get_menus_with_non_existing_restaurant_id() {
            assertThatCode(() -> simpleMenuRepository.restaurantMenus(222))
                .isInstanceOf(NoSuchElementException.class);

        }

        @Test
        void fails_to_get_menus_with_empty_restaurant_id() {
            assertThatCode(() -> simpleMenuRepository.restaurantMenus(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_get_menus_with_non_existing_menu_id() {
            assertThatCode(() -> simpleMenuRepository.menu(222))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_get_menus_with_empty_menu_id() {
            assertThatCode(() -> simpleMenuRepository.menu(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        @Test
        void can_create_restaurant_menu() {
            //given
            Set<Section> sections = new HashSet<>();

            SimpleMenu simpleMenu = new SimpleMenu(4, "Kids Menu", sections);

            //when
            simpleMenuRepository.create(1, simpleMenu.name());

            //then
            List<Menu> menus = simpleMenuRepository.menus();
            assertThat(menus).hasSize(4);
        }

        @Test
        void fails_to_create_a_restaurant_menu_with_empty_menu_name() {
            assertThatCode(() -> simpleMenuRepository.create(1, ""))
                .hasMessageContaining("menu name cannot be empty");
        }

        @Test
        void fails_to_create_a_restaurant_menu_with_null_menu_name() {
            assertThatCode(() -> simpleMenuRepository.create(1, null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_create_a_restaurant_menu_with_null_restaurant_id() {
            assertThatCode(() -> simpleMenuRepository.create(null, "kids menu"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_create_a_restaurant_menu_with_non_existing_restaurant_id() {
            assertThatCode(() -> simpleMenuRepository.create(333, "kids menu"))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        @Sql(statements = "INSERT INTO menu (id, name, restaurant_id) VALUES (4, 'section to delete', 2);")
        void can_delete_a_menu() {
            // given
            simpleMenuRepository.delete(4);

            // when/then
            assertThat(simpleMenuRepository.menus().size()).isEqualTo(3);
        }

        @Test
        void fails_to_delete_a_menu_with_non_existing_menu_id() {
            assertThatCode(() -> simpleMenuRepository.delete(222))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_delete_a_menu_with_empty_menu_id() {
            assertThatCode(() -> simpleMenuRepository.delete(null))
                .hasMessageContaining("menu id cannot be empty");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {
        @Test
        void can_update_menu_name() {
            // given
            simpleMenuRepository.updateMenuName(1, "veg menu");

            // when
            final Optional<Menu> menu = simpleMenuRepository.menu(1);

            // then
            assertThat(menu.map(Menu::name)).contains("veg menu");
        }

        @Test
        void fails_to_update_menu_name_with_non_existing_menu_id() {
            assertThatCode(() -> simpleMenuRepository.updateMenuName(222, "veg menu"))
            .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_update_menu_name_with_empty_menu_id() {
            assertThatCode(() -> simpleMenuRepository.updateMenuName(null, "veg menu"))
                .hasMessageContaining("menu id cannot be empty");
        }


        @Test
        void fails_to_update_menu_name_with_empty_menu_name() {
            assertThatCode(() -> simpleMenuRepository.updateMenuName(1, ""))
                .hasMessageContaining("menu name cannot be empty");
        }
    }
}