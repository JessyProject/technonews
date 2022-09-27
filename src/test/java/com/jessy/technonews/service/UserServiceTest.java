package com.jessy.technonews.service;

import com.jessy.technonews.domain.User;
import com.jessy.technonews.exception.AlreadyExistException;
import com.jessy.technonews.repository.RoleRepository;
import com.jessy.technonews.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;

    private UserServiceImpl underTest;
    //private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        //autoCloseable = MockitoAnnotations.openMocks(this);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        underTest = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    /*@AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }*/

    @Test
    void canGetAllUsers() {
        // when
        underTest.getUsers();
        // then
        verify(userRepository).findAll();
    }

    @Test
    // check if the given user is the same that the user used in UserRepository
    void canSaveUser() throws AlreadyExistException {
        // given
        User user = new User(null, "username", "blabla@ok.fr", "password", false, new ArrayList<>());
        // when
        underTest.saveUser(user);
        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void willThrowWhenEmailIsTaken() throws AlreadyExistException {
        // given
        User user = new User(null, "username", "blabla@ok.fr", "password", false, new ArrayList<>());
        given(userRepository.existsUserByEmail(user.getEmail())).willReturn(true);
        // then
        assertThatThrownBy(() -> underTest.saveUser(user))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Email " + user.getEmail() + " is already taken");
        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenUsernameIsTaken() throws AlreadyExistException {
        // given
        User user = new User(null, "username", "blabla@ok.fr", "password", false, new ArrayList<>());
        given(userRepository.existsUserByUsername(user.getUsername()))
                .willReturn(true);
        // then
        assertThatThrownBy(() -> underTest.saveUser(user))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Username " + user.getUsername() + " is already taken");
        verify(userRepository, never()).save(any());
    }
}