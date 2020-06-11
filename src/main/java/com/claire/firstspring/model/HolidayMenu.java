package com.claire.firstspring.model;

import java.util.List;
import java.util.Set;

import static com.claire.firstspring.model.Feature.GlutenFree;
import static com.claire.firstspring.model.Feature.Keto;
import static com.claire.firstspring.model.Feature.Spicy;
import static com.claire.firstspring.model.Feature.Vegetarian;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.SetUtils.emptySet;
import static org.apache.commons.collections4.SetUtils.hashSet;

public class HolidayMenu implements Menu {

    private Integer id;
    private final Section appetizers = new SimpleSection(id, "Appetizers", appetizerItems());
    private final Section sandwiches = new SimpleSection(id, "Sandwiches", sandwichItems());
    private final Section salads = new SimpleSection(id, "Salads", saladItems());

    @Override
    public Integer id() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Set<Section> sections() {
        return Set.copyOf(
            asList(appetizers, sandwiches, salads)
        );
    }

    private List<Item> saladItems() {
        final SimpleItem silverStarCobb = new SimpleItem(
            id, "Silver Star Cobb",
            "avocado bacon blue cheese",
            14.0,
            hashSet(GlutenFree, Keto)
        );
        final SimpleItem strawberryPecan = new SimpleItem(
            id, "Strawberry Pecan",
            "spring mixed greens with freshly sliced strawberries and candied pecans",
            13.0,
            hashSet(Vegetarian)
        );
        return asList(silverStarCobb, strawberryPecan);
    }

    private List<Item> sandwichItems() {
        final SimpleItem cheeseBurger = new SimpleItem(
            id, "Cheese Burger",
            "Certified Angus beef topped with American cheese",
            11.0,
            emptySet()
        );
        final SimpleItem smokedTurkey = new SimpleItem(
            id, "Smoked Turkey",
            "turkey breast with bistro sauce",
            10.0,
            hashSet(Spicy)
        );
        return asList(cheeseBurger, smokedTurkey);
    }

    private List<Item> appetizerItems() {
        final SimpleItem warmMozzarella = new SimpleItem(
            id, "Warm Mozzarella",
            "creamy cheese baked and topped with reduction",
            15.0,
            hashSet(GlutenFree, Spicy)
        );
        final SimpleItem jumboCrabClaws = new SimpleItem(
            id, "Jumbo Crab Claws",
            "claws marinated with signature sauce",
            19.0,
            emptySet()
        );
        return asList(warmMozzarella, jumboCrabClaws);
    }

    @Override
    public String toString() {
        return "HolidayMenu{" +
            sections() +
            "}";
    }
}
