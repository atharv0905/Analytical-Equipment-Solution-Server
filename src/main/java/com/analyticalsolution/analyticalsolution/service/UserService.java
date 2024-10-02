package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    public int createUser(User user, MultipartFile profileImage) {
        try {
            User existingUser = userRepository.findUserByUsername(user.getUsername());
            if (existingUser != null) {
                log.error("User with username " + user.getUsername() + " already exists.");
                return -1;
            }

            String sql = "INSERT INTO users (id, username, name, password, email, phone, address, roles, profile_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String rolesJson = objectMapper.writeValueAsString(user.getRoles());
            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

            user.setId(UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save profile image and get the path
            String profileImagePath = utilityService.saveProfileImage(profileImage, user.getUsername());

            return jdbcTemplate.update(sql,
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhone(),
                    addressJson,
                    rolesJson,
                    profileImagePath);
        } catch (Exception e) {
            log.error("Unexpected error creating user: " + e.getMessage());
            return -1;
        }
    }

    // Update existing user
    public int updateUser(User user, MultipartFile profileImage) {
        try {
            // Check if the user exists
            User existingUser = userRepository.findUserById(user.getId());
            if (existingUser == null) {
                log.error("User with ID " + user.getId() + " not found.");
                return -1;
            }

            String sql = "UPDATE users SET username = ?, name = ?, password = ?, email = ?, phone = ?, address = ?, roles = ?, profile_path = ? WHERE id = ?";

            // Convert roles and addresses to JSON
            String rolesJson = objectMapper.writeValueAsString(user.getRoles());
            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

            // If password is being updated, encode the new password
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword());  // Keep existing password if not updated
            }

            // If a new profile image is provided, save it and update the path
            String profileImagePath = existingUser.getProfile_path();  // Default to the existing image path
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImagePath = utilityService.saveProfileImage(profileImage, user.getUsername());
            }

            return jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhone(),
                    addressJson,
                    rolesJson,
                    profileImagePath,
                    user.getId());  // Where condition
        } catch (Exception e) {
            log.error("Unexpected error updating user: " + e.getMessage());
            return -1;
        }
    }


    // Fetch user profile image
    public File getUserProfile(String username) {
        try {
            File profileImage = utilityService.fetchProfileImage(username);
            if (profileImage == null || !profileImage.exists()) {
                log.error("Profile image for user " + username + " not found.");
                return null;  // Return null if the profile image doesn't exist
            }
            return profileImage;
        } catch (Exception e) {
            log.error("Error fetching profile image for user " + username + ": " + e.getMessage());
            return null;  // Return null on error
        }
    }


}
