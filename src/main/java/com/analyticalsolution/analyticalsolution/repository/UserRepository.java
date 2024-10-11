/**
 * File: UserRepository.java
 * Author: Atharv Mirgal
 * Description: Repository for handling database interactions for User entities using JdbcTemplate.
 *              This class provides methods to retrieve user details based on username or user ID,
 *              mapping the database results to User objects, including parsing JSON fields for
 *              addresses and roles.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */

package com.analyticalsolution.analyticalsolution.repository;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Find user by username
    public User findUserByUsername(String username){
        try{
            String sql = "SELECT * FROM users WHERE username = ?";

            return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getLong("phone"));

                String addressJson = rs.getString("address");
                String rolesJson = rs.getString("roles");
                try {
                    user.setAddresses(objectMapper.readValue(addressJson, new TypeReference<ArrayList<String>>() {}));
                    user.setRoles(objectMapper.readValue(rolesJson, new TypeReference<ArrayList<String>>() {}));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                return user;
            });

        }catch (Exception e){
            log.error("Error finding user: " + e);
            return null;
        }
    }

    // Find user by ID
    public User findUserById(String userId) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";

            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getLong("phone"));

                String addressJson = rs.getString("address");
                String rolesJson = rs.getString("roles");
                try {
                    user.setAddresses(objectMapper.readValue(addressJson, new TypeReference<ArrayList<String>>() {}));
                    user.setRoles(objectMapper.readValue(rolesJson, new TypeReference<ArrayList<String>>() {}));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                return user;
            });

        } catch (Exception e) {
            log.error("Error finding user by ID: " + e.getMessage());
            return null;
        }
    }

}
