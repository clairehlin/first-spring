package com.claire.firstspring.commandline;

import com.claire.firstspring.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RestaurantCommandLine implements CommandLineRunner {

    @Autowired
    private RestaurantService restaurantService;

    @Override
    public void run(String... args) {
        System.out.println("args: " + Arrays.toString(args));
        System.out.println(restaurantService.list());
        if (args.length > 0) {
        } else {
        }
    }
}
