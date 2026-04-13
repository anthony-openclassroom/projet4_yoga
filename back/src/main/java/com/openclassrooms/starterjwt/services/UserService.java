package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void delete(Long id) {
        this.userRepository.deleteById(id);
    }

    public boolean isOwner(Long id, String email) {
        User user = this.userRepository.findById(id).orElse(null);
        return user != null && user.getEmail().equals(email);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean isAdmin(String email) {
        User user = this.userRepository.findByEmail(email).orElse(null);
        return user != null && user.isAdmin();
    }

    public User register(String email, String lastName, String firstName, String rawPassword) {
        User user = new User(email, lastName, firstName, passwordEncoder.encode(rawPassword), false);
        return this.userRepository.save(user);
    }

    public User save(User user) {
        return this.userRepository.save(user);
    }
}
