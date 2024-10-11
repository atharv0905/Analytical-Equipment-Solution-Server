package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Update user details
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            // Call service method to update the user
            int updateStatus = userService.updateUser(user);

            if (updateStatus == 1) {
                // If update is successful, return 200 OK
                return new ResponseEntity<>("User updated successfully.", HttpStatus.OK);
            } else if (updateStatus == -1) {
                // If user is not found, return 404
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            } else {
                // For other errors, return 500
                return new ResponseEntity<>("Failed to update user.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error updating user: " + e.getMessage());
            return new ResponseEntity<>("Error updating user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
