package com.jessy.technonews.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username is {}", username); log.info("Password is {}", password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
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
