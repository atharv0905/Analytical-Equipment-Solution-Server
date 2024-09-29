package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Create new user
    public int createUser(User user) {
        try{
            String sql = "INSERT INTO users (id, username, name, password, email, phone, address, roles, profile_path) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String rolesJson = objectMapper.writeValueAsString(user.getRoles());

            String addressJson = objectMapper.writeValueAsString(user.getAddresses());

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            return jdbcTemplate.update(sql, user.getId(), user.getUsername(), user.getName(), user.getPassword(), user.getEmail(), user.getPhone(), addressJson, rolesJson, user.getProfile_path());
        }catch (Exception e){
            log.error("Error creating user: " + e);
            return -1;
        }
    }
}
