package com.jessy.technonews.repository;

import com.jessy.technonews.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void testFindEnabledUsersExist() {
        // given
        User user = new User(null, "username", "blabla@ok.fr", "password", false, new ArrayList<>());
        underTest.save(user);
        // when
        List<User> disabledUsers = underTest.findDisabledUsers();
        // then
        assertThat(disabledUsers).filteredOn(u -> u.getUsername().contains("username"))
                .hasSize(1);
    }

    @Test
    void testFindEnabledUsersNotExist() {
        // given
        User user = new User(null, "username", "blabla@ok.fr", "password", true, new ArrayList<>());
        underTest.save(user);
        // when
        List<User> disabledUsers = underTest.findDisabledUsers();
        // then
        assertThat(disabledUsers).hasSize(0);
    }

}