package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Section;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleSectionRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleSectionRepository simpleSectionRepository;

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_list_all_sections() {
            // given/when
            final List<Section> sections = simpleSectionRepository.sections();

            // then
            assertThat(sections).extracting(Section::id).containsExactlyInAnyOrder(1, 2, 3);
        }

        @Test
        void can_return_a_section_with_section_id() {
            //given/when
            final Optional<Section> section = simpleSectionRepository.section(1);

            //then
            assertThat(section.map(Section::name)).contains("Salad Section");
        }

        @Test
        void fail_to_return_a_section_with_missing_section_id() {
            //given/when/then
            assertThatCode(() -> simpleSectionRepository.section(null))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void can_return_a_list_of_sections_with_menu_id() {
            //given/when
            final List<Section> sections = simpleSectionRepository.menuSections(1);

            //then
            assertThat(sections.stream().map(Section::name)).
                containsExactlyInAnyOrder("Salad Section", "Pasta Section");
        }

        @Test
        void fail_to_return_a_list_of_sections_with_missing_menu_id() {
            // when/then
            assertThatCode(() -> simpleSectionRepository.menuSections(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fail_to_return_a_list_of_sections_with_non_existing_menu_id() {
            // when/then
            assertThatCode(() -> simpleSectionRepository.menuSections(222))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Creations {
        @Test
        void can_create_a_section_with_menu_id_and_section_name() {
            // given/when
            simpleSectionRepository.create(2, "bolivia section");

            //then
            List<Section> sections = simpleSectionRepository.menuSections(2);
            assertThat(sections).extracting(Section::name).contains("bolivia section");
        }

        @Test
        void fails_to_create_a_section_with_non_existing_menu_id() {
            assertThatCode(() -> simpleSectionRepository.create(222, "bolivia section"))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_create_a_section_with_null_menu_id() {
            assertThatCode(() -> simpleSectionRepository.create(null, "bolivia section"))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_create_a_section_with_empty_section_name() {
            assertThatCode(() -> simpleSectionRepository.create(2, ""))
                .hasMessageContaining("section name cannot be empty");
        }

        @Test
        void fails_to_create_a_section_with_null_section_name() {
            assertThatCode(() -> simpleSectionRepository.create(2, null))
                .hasMessageContaining("section name cannot be null");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        @Sql(statements = "INSERT INTO section (id, name, menu_id) VALUES (5, 'section to delete', 2);")
        void can_delete_a_section_with_section_id() {
            // given
            simpleSectionRepository.deleteSection(5);

            // when
            final List<Section> sections = simpleSectionRepository.sections();

            //then
            assertThat(sections).extracting(Section::name).doesNotContain("section to delete");
        }

        @Test
        void fails_to_delete_a_section_with_empty_section_id() {
            assertThatCode(() -> simpleSectionRepository.deleteSection(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_delete_a_section_with_non_existing_section_id() {
            assertThatCode(() -> simpleSectionRepository.deleteSection(222))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("could not find section with section id");
        }

        @Test
        void fails_to_delete_a_section_with_menu_id_being_used() {
            assertThatCode(() -> simpleSectionRepository.deleteSection(1))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class UpdatingSectionName {
        @Test
        void can_update_a_section_with_section_id_and_section_name() {
            // given
            simpleSectionRepository.updateSectionName(1, "Italian section");

            // when
            final Optional<Section> section = simpleSectionRepository.section(1);

            // then
            assertThat(section.map(Section::name)).contains("Italian section");

        }

        @Test
        void fails_to_update_a_section_with_non_existing_section_id() {
            assertThatCode(() -> simpleSectionRepository.updateSectionName(222, "chinese section"))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_update_a_section_with_empty_section_name() {
            assertThatCode(() -> simpleSectionRepository.updateSectionName(1, null))
                .isInstanceOf(NullPointerException.class);
        }
    }

}