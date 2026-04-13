package com.openclassrooms.starterjwt.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SessionDtoTest {

    @Test
    void noArgsConstructor_createsEmptyDto() {
        SessionDto dto = new SessionDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();
        List<Long> users = Arrays.asList(1L, 2L);

        SessionDto dto = new SessionDto(1L, "Morning Yoga", date, 2L, "A great session", users, now, now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Morning Yoga");
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getTeacher_id()).isEqualTo(2L);
        assertThat(dto.getDescription()).isEqualTo("A great session");
        assertThat(dto.getUsers()).containsExactly(1L, 2L);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        SessionDto dto = new SessionDto();
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(5L);
        dto.setName("Evening Yoga");
        dto.setDate(date);
        dto.setTeacher_id(3L);
        dto.setDescription("Relaxing session");
        dto.setUsers(Arrays.asList(10L));
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Evening Yoga");
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getTeacher_id()).isEqualTo(3L);
        assertThat(dto.getDescription()).isEqualTo("Relaxing session");
        assertThat(dto.getUsers()).containsExactly(10L);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_returnsTrue_forEqualDtos() {
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();
        SessionDto dto1 = new SessionDto(1L, "Yoga", date, 2L, "Desc", null, now, now);
        SessionDto dto2 = new SessionDto(1L, "Yoga", date, 2L, "Desc", null, now, now);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void equals_returnsFalse_forDifferentDtos() {
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();
        SessionDto dto1 = new SessionDto(1L, "Yoga", date, 2L, "Desc", null, now, now);
        SessionDto dto2 = new SessionDto(2L, "Pilates", date, 3L, "Other", null, now, now);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void toString_containsClassName() {
        SessionDto dto = new SessionDto();
        assertThat(dto.toString()).contains("SessionDto");
    }
}
