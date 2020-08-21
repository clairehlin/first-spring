package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
@Transactional
class SimpleMenuRepositoryTest {
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
        simpleMenuRepository.create(1, simpleMenu.name());

        //then
        List<Menu> menus = simpleMenuRepository.menus();
        assertThat(menus).hasSize(4);
    }
}