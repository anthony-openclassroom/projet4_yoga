package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionRepositoryIT {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher savedTeacher;

    @BeforeEach
    void setUp() {
        savedTeacher = teacherRepository.save(
                Teacher.builder().lastName("Smith").firstName("Jane").build());
    }

    private Session buildSession(String name) {
        return Session.builder()
                .name(name)
                .date(new Date())
                .description("Test description")
                .teacher(savedTeacher)
                .users(List.of())
                .build();
    }

    @Test
    void save_persistsSession() {
        Session saved = sessionRepository.save(buildSession("Morning Yoga"));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Morning Yoga");
    }

    @Test
    void findById_returnsSession_whenExists() {
        Session saved = sessionRepository.save(buildSession("Evening Yoga"));
        assertThat(sessionRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_returnsEmpty_whenNotExists() {
        assertThat(sessionRepository.findById(9999L)).isEmpty();
    }

    @Test
    void findAll_returnsAllSessions() {
        sessionRepository.save(buildSession("Morning Yoga"));
        sessionRepository.save(buildSession("Evening Yoga"));
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(2);
    }

    @Test
    void deleteById_removesSession() {
        Session saved = sessionRepository.save(buildSession("To Delete"));
        sessionRepository.deleteById(saved.getId());
        assertThat(sessionRepository.findById(saved.getId())).isEmpty();
    }
}
