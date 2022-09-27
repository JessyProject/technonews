package com.jessy.technonews.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.jessy.technonews.domain.Role;
import com.jessy.technonews.domain.User;
import com.jessy.technonews.exception.AlreadyExistException;
import com.jessy.technonews.exception.NotExistException;
import com.jessy.technonews.repository.RoleRepository;
import com.jessy.technonews.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    // method that spring uses to load the users from the database or wherever they might be
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database."));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User saveUser(@NotNull User u) throws AlreadyExistException {
        if(userRepo.existsUserByUsername(u.getUsername())) {
            throw new AlreadyExistException("Username " + u.getUsername() + " is already taken.");
        }
        if(userRepo.existsUserByEmail(u.getEmail())) {
            throw new AlreadyExistException("Email " + u.getEmail() + " is already taken.");
        }
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepo.save(u);
    }

    @Override
    public Role saveRole(@NotNull Role r) throws AlreadyExistException {
        if(roleRepo.existsRoleByName(r.getName())) {
            throw new AlreadyExistException("Name " + r.getName() + " is already taken.");
        }
        return roleRepo.save(r);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        Role role = roleRepo.findByName(roleName).orElseThrow(() -> new NotExistException("Role " + roleName + " does not exist."));
        User user = userRepo.findByUsername(username).orElseThrow(() -> new NotExistException("User " + username + " does not exist."));
        List<Role> currentRoles = user.getRoles();
        currentRoles.add(role);
        user.setRoles(currentRoles);

        Collection<User> currentUsers = role.getUsers();
        currentUsers.add(user);
        role.setUsers(currentUsers);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public List<User> getDisabledUsers() {
       return userRepo.findDisabledUsers();
    }

    @Override
    public Optional<User> getUser(String username) {
        return userRepo.findByUsername(username);
    }
}
