package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class ItemRowMapper implements RowMapper<Item> {

    private final FeatureRepository featureRepository;

    public ItemRowMapper(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        Set<Feature> features = featureRepository.itemFeatures(id);
        return new SimpleItem(
            id,
            name,
            description,
            price,
            features
        );
    }
}
