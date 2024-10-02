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

    // Create new user
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile profileImage) {
        try {
            if (profileImage == null || profileImage.isEmpty()) {
                return new ResponseEntity<>("Profile image is required", HttpStatus.BAD_REQUEST);
            }

            int result = userService.createUser(user, profileImage);
            if (result == -1) {
                // User creation failed (either due to existing user or system error)
                return new ResponseEntity<>("User creation failed: User already exists or invalid data.", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unexpected error creating user: ", e);
            return new ResponseEntity<>("Unexpected error creating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Temporary method to fetch user by username
    @GetMapping("/getUserByUsername/{username}")
    public User getUserByUsername(@PathVariable String username){
        return userRepository.findUserByUsername(username);
    }

    // Temporary method to fetch user by user id
    @GetMapping("/getUserByUID/{uid}")
    public User getUserByUID(@PathVariable String uid){
        return userRepository.findUserById(uid);
    }
}
