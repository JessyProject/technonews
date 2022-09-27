package com.jessy.technonews.api;

import com.jessy.technonews.domain.Role;
import com.jessy.technonews.domain.User;
import com.jessy.technonews.exception.AlreadyExistException;
import com.jessy.technonews.filter.CustomProvider;
import com.jessy.technonews.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final CustomProvider customProvider;
    private final UserDetailsService userDetailsService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable(value = "username") String username) {
        return userService.getUser(username).map(user -> ResponseEntity.ok().body(user)).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "user not found for this username : " + username
                ));
    }

    @GetMapping("/users/disable")
    public ResponseEntity<List<User>> getDisabledUsers() {
        return ResponseEntity.ok().body(userService.getDisabledUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try {
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
            return ResponseEntity.created(uri).body(userService.saveUser(user));
        } catch(AlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        try {
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
            return ResponseEntity.created(uri).body(userService.saveRole(role));
        } catch(AlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserName form) {
        try {
            userService.addRoleToUser(form.getUsername(), form.getRoleName());
            return ResponseEntity.ok().build();
        } catch(AlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refresh_token = customProvider.getTokenFromRequest(request);
        if(StringUtils.hasText(refresh_token) && customProvider.validateToken(refresh_token)) {
            try {
                String username = customProvider.getUserUsernameFromJWT(refresh_token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String access_token = customProvider.generateAccessToken((org.springframework.security.core.userdetails.User) userDetails, request);
                customProvider.sendTokens(response, access_token, refresh_token);
            } catch (Exception e) {
                customProvider.errorRefreshingToken(response, e);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }
}

@Data
class RoleToUserName {
    private String username;
    private String roleName;
}
