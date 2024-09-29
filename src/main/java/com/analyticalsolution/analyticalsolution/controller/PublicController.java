package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    // Check whether the server is running
    @GetMapping("/")
    public String serverCheck(){
        return "Server is running...";
    }

    // Create new admin
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody User user){
        try{
            int newAdmin = userService.createUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Error creating admin user: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
