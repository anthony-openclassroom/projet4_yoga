package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        return new User(email, "Doe", "John", "encoded", false);
    }

    @Test
    void save_persistsUser() {
        User saved = userRepository.save(buildUser("test@test.com"));
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByEmail_returnsUser_whenExists() {
        userRepository.save(buildUser("find@test.com"));
        Optional<User> found = userRepository.findByEmail("find@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@test.com");
    }

    @Test
    void findByEmail_returnsEmpty_whenNotExists() {
        Optional<User> found = userRepository.findByEmail("ghost@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_returnsTrue_whenEmailExists() {
        userRepository.save(buildUser("exists@test.com"));
        assertThat(userRepository.existsByEmail("exists@test.com")).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailNotExists() {
        assertThat(userRepository.existsByEmail("nothere@test.com")).isFalse();
    }

    @Test
    void deleteById_removesUser() {
        User saved = userRepository.save(buildUser("delete@test.com"));
        userRepository.deleteById(saved.getId());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}
