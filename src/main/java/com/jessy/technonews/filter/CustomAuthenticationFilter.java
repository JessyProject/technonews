package com.jessy.technonews.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jessy.technonews.util.LoginRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final CustomProvider customProvider;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, CustomProvider customProvider) {
        this.authenticationManager = authenticationManager;
        this.customProvider = customProvider;
    }

    // Called when user tried to log in
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // Read the request body as JSON
        LoginRequest loginRequest;
        try (BufferedReader reader = request.getReader()) {
            ObjectMapper mapper = new ObjectMapper();
            loginRequest = mapper.readValue(reader, LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to read request body", e);
        }

        // Check if username and password are present
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            // Return 400 Bad Request error
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
            return null;
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            // Return 400 Bad Request error
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password is required");
            return null;
        }
        log.info("Username is {}", loginRequest.getUsername()); log.info("Password is {}", loginRequest.getPassword());
        // Create authentication token with username and password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // return the user successfully authenticated
        User user = (User) authentication.getPrincipal();
        String access_token = customProvider.generateAccessToken(user, request);
        String refresh_token = customProvider.generateRefreshToken(user, request);
        customProvider.sendTokens(response, access_token, refresh_token);
    }
}
