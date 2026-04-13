package com.openclassrooms.starterjwt.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void noArgsConstructor_createsEmptyDto() {
        UserDto dto = new UserDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        UserDto dto = new UserDto(1L, "john@test.com", "Doe", "John", false, "secret", now, now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("john@test.com");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.isAdmin()).isFalse();
        assertThat(dto.getPassword()).isEqualTo("secret");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void setters_updateFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto();

        dto.setId(3L);
        dto.setEmail("admin@test.com");
        dto.setLastName("Admin");
        dto.setFirstName("Super");
        dto.setAdmin(true);
        dto.setPassword("hashed");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getEmail()).isEqualTo("admin@test.com");
        assertThat(dto.getLastName()).isEqualTo("Admin");
        assertThat(dto.getFirstName()).isEqualTo("Super");
        assertThat(dto.isAdmin()).isTrue();
        assertThat(dto.getPassword()).isEqualTo("hashed");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_returnsTrue_forEqualDtos() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto1 = new UserDto(1L, "john@test.com", "Doe", "John", false, "pass", now, now);
        UserDto dto2 = new UserDto(1L, "john@test.com", "Doe", "John", false, "pass", now, now);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void equals_returnsFalse_forDifferentDtos() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto1 = new UserDto(1L, "john@test.com", "Doe", "John", false, "pass", now, now);
        UserDto dto2 = new UserDto(2L, "jane@test.com", "Smith", "Jane", true, "pass", now, now);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void toString_containsClassName() {
        UserDto dto = new UserDto();
        assertThat(dto.toString()).contains("UserDto");
    }
}
