package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.SimpleMenu;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan({"com.claire.firstspring.repository", "com.claire.firstspring.service"})
@Import({PersistenceConfig.class})
class SimpleMenuServiceTest {
    @Autowired
    SimpleMenuService simpleMenuService;

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_list_all_menus() {
            // when
            final List<Menu> menus = simpleMenuService.list();

            // then
            assertThat(menus.stream().map(Menu::name)).containsExactlyInAnyOrder(
                "Holiday Menu",
                "Standard Menu",
                "Simple Menu"
            );
        }

        @Test
        void can_get_a_menu_with_menu_id() {
            // when
            Menu menu = simpleMenuService.menu(2);

            // then
            assertThat(menu.name()).isEqualTo("Holiday Menu");
        }

        @Test
        void fails_to_get_a_menu_with_non_existing_menu_id() {
            assertThatCode(() -> simpleMenuService.menu(100))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("menu id 100 does not exist");
        }

        @Test
        void fails_to_get_a_menu_with_empty_menu_id() {
            assertThatCode(() -> simpleMenuService.menu(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        @Test
        void can_add_a_menu_with_restaurant_id() {
            // given
            Menu menu = new SimpleMenu(null, "Christmas Menu", emptySet());

            // when
            simpleMenuService.addMenu(2, menu);

            // then
            assertThat(simpleMenuService.list().stream().map(Menu::name)).contains("Christmas Menu");
        }

        @Test
        void fails_to_add_a_menu_with_menu_id() {
            // given
            Menu menu = new SimpleMenu(100, "Christmas Menu", emptySet());

            // when/then
            assertThatCode(() -> simpleMenuService.addMenu(2, menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("menu id must be null to create a new menu");
        }

        @Test
        void fails_to_add_a_menu_with_empty_restaurant_id() {
            // given
            Menu menu = new SimpleMenu(null, "Christmas Menu", emptySet());

            // when/then
            assertThatCode(() -> simpleMenuService.addMenu(null, menu))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_add_a_menu_to_non_existing_restaurant_id() {
            // given
            Menu menu = new SimpleMenu(null, "Christmas Menu", emptySet());

            // when/then
            assertThatCode(() -> simpleMenuService.addMenu(100, menu))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("restaurant id 100 does not exist");
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {
        @Test
        void can_update_an_existing_menu() {
            // given
            assertThat(simpleMenuService.list().stream().map(Menu::name)).doesNotContain("Christmas Menu");

            Menu menu = new SimpleMenu(2, "Christmas Menu", emptySet());

            // when
            simpleMenuService.updateMenu(menu);

            // then
            assertThat(simpleMenuService.list().stream().map(Menu::name)).contains("Christmas Menu");
        }

        @Test
        void fails_to_update_a_menu_with_empty_menu_id() {
            // given
            Menu menu = new SimpleMenu(null, "Christmas Menu", emptySet());

            // when/then
            assertThatCode(() -> simpleMenuService.updateMenu(menu))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_update_a_menu_with_a_non_existing_menu_id() {
            // given
            Menu menu = new SimpleMenu(100, "Christmas Menu", emptySet());

            // when/then
            assertThatCode(() -> simpleMenuService.updateMenu(menu))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("menu id 100 does not exist");
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        void can_delete_a_menu() {
            // given
            final Menu menu = simpleMenuService.menu(1);
            assertThat(menu.name()).isEqualTo("Simple Menu");

            // when
            simpleMenuService.deleteMenu(1);

            // then
            assertThat(simpleMenuService.list().stream().map(Menu::name)).doesNotContain("Simple Menu");
        }

        @Test
        void fails_to_delete_a_non_existing_menu() {
            // when/then
            assertThatCode(() -> simpleMenuService.deleteMenu(100))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("menu id 100 does not exist");
        }

        @Test
        void fails_to_delete_an_empty_menu_id() {
            // when/then
            assertThatCode(() -> simpleMenuService.deleteMenu(null))
                .isInstanceOf(NullPointerException.class);
        }
    }


}