package com.claire.firstspring.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

class HolidayMenuTest {

    @Test
    void can_get_a_list_of_items() {
        HolidayMenu holidayMenu = new HolidayMenu();
        Set<Section> sections = holidayMenu.sections();

        Section section0 = sections.iterator().next();

        System.out.println("Section name: " + section0.name());
        System.out.println("Section items: " + section0.items());
    }
}