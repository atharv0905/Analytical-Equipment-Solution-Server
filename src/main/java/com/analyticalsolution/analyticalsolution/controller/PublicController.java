package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Check whether the server is running
    @GetMapping("/")
    public String serverCheck(){
        return "Server is running...";
    }

    // Create new admin
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile profileImage) {
        try {
            if (profileImage == null || profileImage.isEmpty()) {
                return new ResponseEntity<>("Profile image is required", HttpStatus.BAD_REQUEST);
            }

            int result = userService.createUser(user, profileImage);
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return new ResponseEntity<>("Error creating user", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getUserByUsername/{username}")
    public User getUser(@PathVariable String username){
        return userRepository.findUserByUsername(username);
    }
}
