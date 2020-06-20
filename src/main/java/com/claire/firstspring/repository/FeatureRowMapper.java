package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeatureRowMapper implements RowMapper<Feature> {
    @Override
    public Feature mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Feature(rs.getString("name"));
    }
}
