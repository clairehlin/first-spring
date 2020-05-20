package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.SimpleMenu;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class MenuRowMapper implements RowMapper<Menu> {
    private final SectionRepository sectionRepository;

    public MenuRowMapper(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return new SimpleMenu(
                id,
                name,
                Set.copyOf(sectionRepository.menuSections(id)));
    }
}
