package com.jessy.technonews.service;

import java.util.List;
import java.util.Optional;

import com.jessy.technonews.domain.Role;
import com.jessy.technonews.domain.User;
import com.jessy.technonews.exception.AlreadyExistException;


public interface UserService {
    User saveUser(User u) throws AlreadyExistException;
    Role saveRole(Role r) throws AlreadyExistException;
    void addRoleToUser(String username, String roleName);
    List<User> getUsers();
    List<User> getDisabledUsers();
    Optional<User> getUser(String username);
}
