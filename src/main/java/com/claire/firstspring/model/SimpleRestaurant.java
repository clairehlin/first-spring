package com.claire.firstspring.model;

import org.springframework.stereotype.Service;

@Service
public class SimpleRestaurant implements Restaurant {
    @Override
    public Menu menu() {
        return new HolidayMenu();
    }
}
