package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private UserDetailsImpl build(Long id, String username) {
        return UserDetailsImpl.builder()
                .id(id)
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("pass")
                .build();
    }

    @Test
    void getAuthorities_returnsEmptySet() {
        assertThat(build(1L, "a@a.com").getAuthorities()).isEmpty();
    }

    @Test
    void accountStatus_methodsReturnTrue() {
        UserDetailsImpl u = build(1L, "a@a.com");
        assertThat(u.isAccountNonExpired()).isTrue();
        assertThat(u.isAccountNonLocked()).isTrue();
        assertThat(u.isCredentialsNonExpired()).isTrue();
        assertThat(u.isEnabled()).isTrue();
    }

    @Test
    void equals_returnsTrue_forSameId() {
        UserDetailsImpl u1 = build(1L, "a@a.com");
        UserDetailsImpl u2 = build(1L, "b@b.com"); // même id, email différent

        assertThat(u1).isEqualTo(u2);
    }

    @Test
    void equals_returnsFalse_forDifferentId() {
        UserDetailsImpl u1 = build(1L, "a@a.com");
        UserDetailsImpl u2 = build(2L, "a@a.com");

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    void equals_returnsFalse_forNull() {
        assertThat(build(1L, "a@a.com")).isNotEqualTo(null);
    }

    @Test
    void equals_returnsFalse_forDifferentType() {
        assertThat(build(1L, "a@a.com")).isNotEqualTo("string");
    }

    @Test
    void equals_returnsTrue_forSameInstance() {
        UserDetailsImpl u = build(1L, "a@a.com");
        assertThat(u).isEqualTo(u);
    }

    @Test
    void getters_returnCorrectValues() {
        UserDetailsImpl u = UserDetailsImpl.builder()
                .id(5L)
                .username("user@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .admin(true)
                .password("secret")
                .build();

        assertThat(u.getId()).isEqualTo(5L);
        assertThat(u.getUsername()).isEqualTo("user@test.com");
        assertThat(u.getFirstName()).isEqualTo("Jane");
        assertThat(u.getLastName()).isEqualTo("Smith");
        assertThat(u.getAdmin()).isTrue();
        assertThat(u.getPassword()).isEqualTo("secret");
    }
}
