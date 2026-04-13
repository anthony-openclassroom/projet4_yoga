package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionService sessionService;

    // ─── create ─────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsSession() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    // ─── delete ─────────────────────────────────────────────────────────────

    @Test
    void delete_callsDeleteById() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    // ─── findAll ────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsList() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(2);
    }

    // ─── getById ────────────────────────────────────────────────────────────

    @Test
    void getById_returnsSession_whenFound() {
        Session session = new Session();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertThat(result).isEqualTo(session);
    }

    @Test
    void getById_returnsNull_whenNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(99L);

        assertThat(result).isNull();
    }

    // ─── update ─────────────────────────────────────────────────────────────

    @Test
    void update_setsIdAndSaves() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(5L, session);

        assertThat(session.getId()).isEqualTo(5L);
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    // ─── participate ────────────────────────────────────────────────────────

    @Test
    void participate_throwsNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_throwsNotFoundException_whenUserNotFound() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userService.findById(2L)).thenReturn(null);

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_throwsBadRequestException_whenAlreadyParticipating() {
        User user = new User("a@a.com", "Doe", "John", "pass", false);
        user.setId(2L);
        Session session = new Session();
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userService.findById(2L)).thenReturn(user);

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void participate_addsUserAndSaves() {
        User user = new User("a@a.com", "Doe", "John", "pass", false);
        user.setId(2L);
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userService.findById(2L)).thenReturn(user);

        sessionService.participate(1L, 2L);

        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository).save(session);
    }

    // ─── noLongerParticipate ────────────────────────────────────────────────

    @Test
    void noLongerParticipate_throwsNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void noLongerParticipate_throwsBadRequestException_whenNotParticipating() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 2L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void noLongerParticipate_removesUserAndSaves() {
        User user = new User("a@a.com", "Doe", "John", "pass", false);
        user.setId(2L);
        Session session = new Session();
        session.setUsers(new ArrayList<>(Arrays.asList(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 2L);

        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository).save(session);
    }
}
