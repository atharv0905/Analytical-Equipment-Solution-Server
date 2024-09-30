package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Create new user
    public int createUser(User user, MultipartFile profileImage) {
        try {
            String sql = "INSERT INTO users (id, username, name, password, email, phone, address, roles, profile_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String rolesJson = objectMapper.writeValueAsString(user.getRoles());
            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

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
            log.error("Error creating user: " + e.getMessage());
            return -1;
        }
    }

}
