/**
 * File: UserService.java
 * Author: Atharv Mirgal
 * Description: Service layer for managing user-related operations such as user creation, password reset, and updates.
 *              Includes functionality for verifying user tokens, deleting users, and checking email verification status.
 *              The service integrates with the database and repository to perform these operations and maintains secure password encoding.
 * Created on: 11/10/2024
 * Last Modified: 27/10/2024
 */


package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.TokenAuthResponse;
import com.analyticalsolution.analyticalsolution.utils.UtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Create new user
    public int createUser(User user) {
        try {
            String checkEmailVerificationSql = "SELECT COUNT(*) FROM email_verification WHERE email = ?";
            int verificationCount = jdbcTemplate.queryForObject(checkEmailVerificationSql, new Object[]{user.getEmail()}, Integer.class);

            if(verificationCount > 0){
                String checkVerificationStatus = "SELECT verified FROM email_verification WHERE email = ?";
                Boolean isVerified = jdbcTemplate.queryForObject(checkVerificationStatus, new Object[]{user.getEmail()}, Boolean.class);

                if (isVerified == null || !isVerified) {
                    log.error("Email not verified for: " + user.getEmail());
                    return -2;
                }
            }else {
                log.error("Email does not exist in the verification table: " + user.getEmail());
                return -2;
            }
            User existingUser = userRepository.findUserByUsername(user.getUsername());
            if (existingUser != null) {
                log.error("User with username " + user.getUsername() + " already exists.");
                return -1;
            }

            String sql = "INSERT INTO users (id, username, name, password, email, phone, address, roles) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            String rolesJson = objectMapper.writeValueAsString(user.getRoles());
            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

            user.setId(UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            String deleteEmailVerificationSql = "DELETE FROM email_verification WHERE email = ?";
            jdbcTemplate.update(deleteEmailVerificationSql, user.getEmail());

            return jdbcTemplate.update(sql,
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhone(),
                    addressJson,
                    rolesJson);
        } catch (Exception e) {
            log.error("Unexpected error creating user: " + e.getMessage());
            return -1;
        }
    }

    // Reset password
    public int resetPassword(String password){
        try{
            // Check if the user exists
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());
            if (existingUser == null) {
                log.error("User not found.");
                return -1;
            }

            String sql = "UPDATE users SET password = ? WHERE id = ?";

            // If password is being updated, encode the new password
            if (password != null && !password.isEmpty()) {
                password = passwordEncoder.encode(password);
            }

            return jdbcTemplate.update(sql,
                    password,
                    existingUser.getId());

        } catch (Exception e) {
            log.error("Unexpected error resetting password: " + e.getMessage());
            return -1;
        }
    }

    // Update existing user
    public int updateUser(User user) {
        try {
            // Check if the user exists
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());
            if (existingUser == null) {
                log.error("User not found.");
                return -1;
            }

            String sql = "UPDATE users SET username = ?, name = ?, email = ?, phone = ?, address = ?, roles = ? WHERE id = ?";

            // Convert roles and addresses to JSON
            String rolesJson = objectMapper.writeValueAsString(user.getRoles());
            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

            // If password is being updated, encode the new password
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword());  // Keep existing password if not updated
            }

            return jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    addressJson,
                    rolesJson,
                    existingUser.getId());  // Where condition
        } catch (Exception e) {
            log.error("Unexpected error updating user: " + e.getMessage());
            return -1;
        }
    }

    // Delete user
    public void deleteUser(){
        try{
            // Check if the user exists
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());
            if (existingUser == null) {
                log.error("User not found.");
            }

            String sql = "DELETE FROM users WHERE id = ?";
            jdbcTemplate.update(sql, existingUser.getId());
        } catch (Exception e) {
            log.error("Error deleting user: " + e.getMessage());
        }
    }

    // Verify user token
    public TokenAuthResponse verifyToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());
            if (existingUser != null) {
                return new TokenAuthResponse(existingUser, true);
            } else {
                return new TokenAuthResponse(false);
            }
        } catch (Exception ex) {
            return new TokenAuthResponse(false);
        }
    }

}
