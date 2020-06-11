package com.claire.firstspring.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Repository
public class SimpleIdGeneratingRepository implements IdGeneratingRepository {
    private final JdbcTemplate jdbcTemplate;

    public SimpleIdGeneratingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int nextId(MainTableAwareRepository mainTableAwareRepository) {
        final String sql = "SELECT MAX(id) FROM " + mainTableAwareRepository.getMainTableName();
        return defaultIfNull(
            jdbcTemplate.queryForObject(sql, Integer.class),
            -1
        ) + 1;
    }
}
