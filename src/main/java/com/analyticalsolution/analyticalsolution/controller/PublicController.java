package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.LoginRequest;
import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.service.UserDetailsServiceImpl;
import com.analyticalsolution.analyticalsolution.service.UserService;
import com.analyticalsolution.analyticalsolution.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    // Check whether the server is running
    @GetMapping("/")
    public String serverCheck(){
        return "Server is running...";
    }

    // Create new user
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            int result = userService.createUser(user);
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

    // Login user
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwtToken = jwtUtils.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
