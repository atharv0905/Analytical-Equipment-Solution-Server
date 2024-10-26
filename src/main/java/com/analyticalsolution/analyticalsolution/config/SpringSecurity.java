/**
 * File: SpringSecurity.java
 * Author: Atharv Mirgal
 * Description: This configuration file sets up Spring Security for the application, enabling
 *              JWT-based authentication and specifying security policies. It configures
 *              authentication for "/user/**", "/email/**" endpoints, disables CSRF for stateless sessions,
 *              and integrates a JWT filter to validate tokens for secure requests.
 *              It also provides beans for PasswordEncoder using BCrypt and AuthenticationManager
 *              for managing authentication processes.
 * Created on: 11/10/2024
 * Last Modified: 15/10/2024
 */

package com.analyticalsolution.analyticalsolution.config;

import com.analyticalsolution.analyticalsolution.filter.JwtFilter;
import com.analyticalsolution.analyticalsolution.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/user/**", "/cart/**", "/order/**", "/payment/**", "/email/**").authenticated()
                                .requestMatchers("/product/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().permitAll())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
