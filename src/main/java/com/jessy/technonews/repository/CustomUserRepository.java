package com.jessy.technonews.repository;

import com.jessy.technonews.domain.User;

import java.util.List;

public interface CustomUserRepository {
    List<User> findDisabledUsers();
}
