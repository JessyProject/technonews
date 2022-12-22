package com.jessy.technonews.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class CustomProvider {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    @Value("${app.jwtAccessExpirationInMs}")
    private int jwtAccessExpirationInMs;
    @Value("${app.jwtRefreshExpirationInMs}")
    private int jwtRefreshExpirationInMs;


    /**
     * Génère un token JWT d'accès avec l'username de l'utilisateur en tant que sujet et la durée de vie spécifiée dans jwtAccessExpirationInMs
     * @param user l'utilisateur pour lequel le token est généré
     * @param request la requête HTTP en cours
     * @return le token JWT généré
     */
    public String generateAccessToken(User user, HttpServletRequest request) {
        return generateToken(user, request, jwtAccessExpirationInMs, ACCESS_TOKEN_TYPE);
    }

    /**
     * Génère un token JWT de rafraîchissement avec l'username de l'utilisateur en tant que sujet et la durée de vie spécifiée dans jwtRefreshExpirationInMs
     * @param user l'utilisateur pour lequel le token est généré
     * @param request la requête HTTP en cours
     * @return le token JWT généré
     */
    public String generateRefreshToken(User user, HttpServletRequest request) {
        return generateToken(user, request, jwtRefreshExpirationInMs, REFRESH_TOKEN_TYPE);
    }

    /**
    * Génère un token JWT avec l'username de l'utilisateur en tant que sujet, la durée de vie spécifiée en paramètre et le type de token spécifié en paramètre
    * @param user l'utilisateur pour lequel le token est généré
    * @param request la requête HTTP en cours
    * @param expirationInMs la durée de vie du token en millisecondes
    * @param tokenType le type du token ("access" ou "refresh")
    * @return le token JWT généré
    */
    private String generateToken(User user, HttpServletRequest request, long expirationInMs, String tokenType) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(request.getRequestURL().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationInMs))
                .claim("tokenType", tokenType)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Envoie une réponse HTTP avec un code d'erreur "FORBIDDEN" et un message d'erreur dans le corps de la réponse
     * @param response la réponse HTTP à envoyer
     * @param e l'exception à logger et à inclure dans le message d'erreur
     * @throws IOException si une erreur est survenue lors de l'écriture de la réponse HTTP
     */
    public void errorRefreshingToken(HttpServletResponse response, Exception e) throws IOException {
        log.error("Error refreshing token", e);
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    /**
     * Récupère l'username du sujet du token JWT
     * @param token le token JWT à parser
     * @return l'username du sujet du token JWT
     */
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
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
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
