package com.jessy.technonews.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomProvider {
    //@Value("${app.jwtSecret}")
    private String jwtSecret = "secret";
    //@Value("${app.jwtAccessExpirationInMs}")
    private int jwtAccessExpirationInMs = 10*60*1000;
    //@Value("${app.jwtRefreshExpirationInMs}")
    private int jwtRefreshExpirationInMs = 30*60*1000;


    public String generateAccessToken(User user, HttpServletRequest request) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(request.getRequestURL().toString())
                .setIssuer(request.getRequestURL().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(User user, HttpServletRequest request) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(request.getRequestURL().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public void errorRefreshingToken(HttpServletResponse response, Exception e) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    public String getUserUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // send tokens in body
    public void sendTokens(HttpServletResponse response, String access_token, String refresh_token) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
