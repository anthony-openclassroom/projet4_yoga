package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ─── delete ─────────────────────────────────────────────────────────────

    @Test
    void delete_callsDeleteById() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    // ─── isOwner ────────────────────────────────────────────────────────────

    @Test
    void isOwner_returnsTrue_whenEmailMatches() {
        User user = new User("test@test.com", "Doe", "John", "pass", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.isOwner(1L, "test@test.com")).isTrue();
    }

    @Test
    void isOwner_returnsFalse_whenEmailDoesNotMatch() {
        User user = new User("test@test.com", "Doe", "John", "pass", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.isOwner(1L, "other@test.com")).isFalse();
    }

    @Test
    void isOwner_returnsFalse_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(userService.isOwner(99L, "test@test.com")).isFalse();
    }

    // ─── findById ───────────────────────────────────────────────────────────

    @Test
    void findById_returnsUser_whenFound() {
        User user = new User("test@test.com", "Doe", "John", "pass", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_returnsNull_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.findById(99L);

        assertThat(result).isNull();
    }

    // ─── findByEmail ────────────────────────────────────────────────────────

    @Test
    void findByEmail_returnsUser_whenFound() {
        User user = new User("test@test.com", "Doe", "John", "pass", false);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@test.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findByEmail_returnsNull_whenNotFound() {
        when(userRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        User result = userService.findByEmail("none@test.com");

        assertThat(result).isNull();
    }

    // ─── existsByEmail ──────────────────────────────────────────────────────

    @Test
    void existsByEmail_returnsTrue() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThat(userService.existsByEmail("test@test.com")).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse() {
        when(userRepository.existsByEmail("none@test.com")).thenReturn(false);

        assertThat(userService.existsByEmail("none@test.com")).isFalse();
    }

    // ─── isAdmin ────────────────────────────────────────────────────────────

    @Test
    void isAdmin_returnsTrue_whenUserIsAdmin() {
        User user = new User("admin@test.com", "Admin", "Super", "pass", true);
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        assertThat(userService.isAdmin("admin@test.com")).isTrue();
    }

    @Test
    void isAdmin_returnsFalse_whenUserIsNotAdmin() {
        User user = new User("user@test.com", "Doe", "John", "pass", false);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThat(userService.isAdmin("user@test.com")).isFalse();
    }

    @Test
    void isAdmin_returnsFalse_whenUserNotFound() {
        when(userRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThat(userService.isAdmin("none@test.com")).isFalse();
    }

    // ─── register ───────────────────────────────────────────────────────────

    @Test
    void register_encodesPasswordAndSavesUser() {
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        User savedUser = new User("new@test.com", "Doe", "John", "encodedPassword", false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register("new@test.com", "Doe", "John", "rawPassword");

        assertThat(result).isEqualTo(savedUser);
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(any(User.class));
    }

    // ─── save ───────────────────────────────────────────────────────────────

    @Test
    void save_returnsPersistedUser() {
        User user = new User("test@test.com", "Doe", "John", "pass", false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }
}
