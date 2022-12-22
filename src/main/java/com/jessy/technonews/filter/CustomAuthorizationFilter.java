package com.jessy.technonews.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"SpringJavaAutowiringInspection", "NullableProblems"})
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final CustomProvider customProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Filtre les requêtes HTTP pour déterminer si l'utilisateur a accès à l'application ou non.
     * Si un token JWT est présent dans la requête et que ce token est valide, crée un objet d'authentification pour l'utilisateur
     * associé au token et place cet objet dans le contexte de sécurité.
     * @param request la requête HTTP en cours
     * @param response la réponse HTTP en cours
     * @param filterChain la chaîne de filtres en cours
     * @throws ServletException si un problème survient lors du filtrage de la requête
     * @throws IOException si un problème survient lors de la lecture ou de l'écriture de la réponse
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String token = customProvider.getTokenFromRequest(request);
            if(StringUtils.hasText(token) && customProvider.validateToken(token)) {
                try {
                    String username = customProvider.getUserUsernameFromJWT(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (Exception e) {
                    customProvider.errorRefreshingToken(response, e);
                }
            }
            filterChain.doFilter(request, response);
    }
}
