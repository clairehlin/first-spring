package com.claire.firstspring.commandline;

import com.claire.firstspring.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RestaurantCommandLine implements CommandLineRunner {

    @Autowired
    private Restaurant restaurant;

    @Override
    public void run(String... args) {
        System.out.println("args: " + Arrays.toString(args));
        System.out.println(restaurant.menu());
        if (args.length > 0) {
        } else {
        }
    }
}
