package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleRestaurantRepository implements RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RestaurantRowMapper restaurantRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;


    public SimpleRestaurantRepository(JdbcTemplate jdbcTemplate, RestaurantRowMapper restaurantRowMapper, IdGeneratingRepository idGeneratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.restaurantRowMapper = restaurantRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
    }

    @Override
    public List<Restaurant> restaurants() {
        final List<Restaurant> restaurants = jdbcTemplate.query(
            "SELECT * FROM restaurant LIMIT 101",
            restaurantRowMapper
        );
        if (restaurants.size() > 100) {
            final Integer restaurantCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurant",
                Integer.class
            );
            throw new RuntimeException("too many rows of restaurants in database: " + restaurantCount);
        }
        return restaurants;
    }

    @Override
    public Optional<Restaurant> restaurant(Integer id) {
        Validate.notNull(id, "restaurant id cannot be empty");
        validateRestaurantExists(id);

        List<Restaurant> listOfRestaurant = jdbcTemplate.query(
            "SELECT * FROM restaurant LIMIT 101",
            restaurantRowMapper
        );
        if (listOfRestaurant.size() > 100) {
            throw new RuntimeException("too many rows of restaurants in database");
        }
        return listOfRestaurant
            .stream()
            .filter(r -> r.id().equals(id))
            .findFirst();
    }

    private void validateRestaurantExists(Integer id) {
        final boolean restaurantExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
            "SELECT count(*) FROM restaurant WHERE id = ?",
            toArray(id),
            Integer.class
            )
        ) > 0;

        if (!restaurantExists) {
            throw new NoSuchElementException(String.format("restaurant id %s does not exist", id));
        }
    }

    @Override
    public Restaurant create(String restaurantName) {
        Validate.notBlank(restaurantName, "restaurant name cannot be blank");

        final int restaurantId = idGeneratingRepository.nextId(this);

        jdbcTemplate.update(
            "INSERT INTO restaurant (id, name) VALUES (?, ?)",
            restaurantId,
            restaurantName
        );

        return new SimpleRestaurant(
            restaurantId,
            restaurantName,
            emptySet()
        );
    }

    @Override
    public void delete(Integer restaurantId) {
        Validate.notNull(restaurantId);
        validateRestaurantExists(restaurantId);

        final int deleted = jdbcTemplate.update(
            "DELETE from restaurant WHERE id = ?",
            restaurantId
        );

        if (deleted < 1) {
            throw new NoSuchElementException(
                "could not delete restaurant with id " + restaurantId +
                    ", perhaps it does not exist."
            );
        }

    }

    @Override
    public void updateRestaurantName(Integer id, String name) {
        Validate.notNull(id, "restaurant id cannot be null");
        Validate.notBlank(name, "restaurant name cannot be empty");
        validateRestaurantExists(id);

        final int updated = jdbcTemplate.update(
            "UPDATE restaurant SET name = ? WHERE id = ?",
            name,
            id
        );

        if (updated < 1) {
            throw new NoSuchElementException(
                String.format(
                    "could not update restaurant name with new name %s, perhaps restaurant id %s does not exist.",
                    name,
                    id
                )
            );
        }

    }

    public Optional<Restaurant> restaurant2(Integer id) {
        Restaurant restaurant = jdbcTemplate.queryForObject(
            "SELECT * FROM restaurant WHERE id = ?",
            toArray(id),
            restaurantRowMapper
        );
        return Optional.ofNullable(restaurant);
    }

    @Override
    public String getMainTableName() {
        return "restaurant";
    }
}
