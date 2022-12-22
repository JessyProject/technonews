package com.jessy.technonews.security;

import com.jessy.technonews.filter.CustomAuthenticationFilter;
import com.jessy.technonews.filter.CustomAuthorizationFilter;
import com.jessy.technonews.filter.CustomProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;
    private final CustomProvider customProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Autowired AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager, customProvider);
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/login/**", "/api/token/refresh/**", "/api/docs/**", "/swagger-ui/**").permitAll()
                .antMatchers(GET, "/api/user/**").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers(POST, "/api/user/save/**").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(customProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
