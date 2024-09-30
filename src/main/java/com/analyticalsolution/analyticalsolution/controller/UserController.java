package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<FileSystemResource> getUserProfile(@PathVariable String username) {
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
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // User profile not found
            }
        } catch (Exception e) {
            log.error("Error fetching profile: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
