package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Get user profile by usernam
    @GetMapping("/getUserProfile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        try {
            File userProfile = userService.getUserProfile(username);
            if (userProfile != null && userProfile.exists()) {
                // Determine the content type based on the file extension
                String contentType = Files.probeContentType(userProfile.toPath());

                // Create a FileSystemResource from the file
                FileSystemResource fileResource = new FileSystemResource(userProfile);

                // Set the response headers
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + userProfile.getName());

                // Return the file as a response entity
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(fileResource);
            } else {
                // If the profile image is not found, return 404
                return new ResponseEntity<>("Profile image for user " + username + " not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error fetching profile for user " + username + ": " + e.getMessage());
            // Return a 400 Bad Request status if an error occurs
            return new ResponseEntity<>("Error fetching profile for user " + username + ": " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Update user details
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@ModelAttribute("user") User user, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            // Call service method to update the user
            int updateStatus = userService.updateUser(user, profileImage);

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
