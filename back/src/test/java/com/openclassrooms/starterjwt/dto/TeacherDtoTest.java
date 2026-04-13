package com.openclassrooms.starterjwt.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherDtoTest {

    @Test
    void noArgsConstructor_createsEmptyDto() {
        TeacherDto dto = new TeacherDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        TeacherDto dto = new TeacherDto(1L, "Doe", "John", now, now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto = new TeacherDto();

        dto.setId(2L);
        dto.setLastName("Smith");
        dto.setFirstName("Jane");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getLastName()).isEqualTo("Smith");
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_returnsTrue_forEqualDtos() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto1 = new TeacherDto(1L, "Doe", "John", now, now);
        TeacherDto dto2 = new TeacherDto(1L, "Doe", "John", now, now);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void equals_returnsFalse_forDifferentDtos() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto1 = new TeacherDto(1L, "Doe", "John", now, now);
        TeacherDto dto2 = new TeacherDto(2L, "Smith", "Jane", now, now);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void toString_containsClassName() {
        TeacherDto dto = new TeacherDto();
        assertThat(dto.toString()).contains("TeacherDto");
    }
}
