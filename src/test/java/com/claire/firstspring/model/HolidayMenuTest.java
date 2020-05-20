package com.claire.firstspring.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.claire.firstspring.model.Feature.GlutenFree;
import static org.apache.commons.collections4.SetUtils.hashSet;
import static org.junit.jupiter.api.Assertions.*;

class HolidayMenuTest {

    @Test
    void can_get_a_list_of_items(){
        HolidayMenu holidayMenu = new HolidayMenu();
        Set<Section> sections = holidayMenu.sections();

        Section section0 = sections.iterator().next();

        System.out.println("Section name: " + section0.name());
        System.out.println("Section items: " + section0.items());
    }
}