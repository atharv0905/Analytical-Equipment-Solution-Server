/**
 * File: UserController.java
 * Author: Atharv Mirgal
 * Description: REST controller for user account management. Provides endpoints for updating user information,
 *              resetting passwords, and deleting accounts, with response handling to communicate operation status.
 *              Interacts with UserService for secure, efficient handling of user data and logs errors for troubleshooting.
 * Created on: 11/10/2024
 * Last Modified: 28/10/2024
 */


package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.entity.UserAddress;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.UserResponse;
import com.analyticalsolution.analyticalsolution.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    // Save new address
    @PostMapping("/save-new-address")
    public ResponseEntity<?> saveNewAddress(@RequestBody UserAddress address) {
        try {
            // Call service method to update the user
            int saveStatus = userService.saveNewAddress(address, null);

            if (saveStatus == 1) {
                // If update is successful, return 200 OK
                return new ResponseEntity<>("User address successfully.", HttpStatus.OK);
            } else if (saveStatus == -2) {
                // If user is not found, return 404
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            } else {
                // For other errors, return 500
                return new ResponseEntity<>("Failed to save user address.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error saving user addres: " + e.getMessage());
            return new ResponseEntity<>("Error saving user address: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Update address
    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestBody UserAddress address) {
        try {
            // Call service method to update the user
            int saveStatus = userService.updateAddress(address);

            if (saveStatus == 1) {
                // If update is successful, return 200 OK
                return new ResponseEntity<>("User address updated successfully.", HttpStatus.OK);
            } else if (saveStatus == -2) {
                // If user is not found, return 404
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            } else {
                // For other errors, return 500
                return new ResponseEntity<>("Failed to update user address.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error saving user addres: " + e.getMessage());
            return new ResponseEntity<>("Error updating user address: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete address
    @DeleteMapping("/delete-address/{addressID}")
    public ResponseEntity<?> deleteAddress(@PathVariable String addressID) {
        try {
            // Call service method to update the user
            int saveStatus = userService.deleteAddress(addressID);

            if (saveStatus == 1) {
                // If update is successful, return 200 OK
                return new ResponseEntity<>("User address deleted successfully.", HttpStatus.OK);
            } else if (saveStatus == -2) {
                // If user is not found, return 404
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            } else {
                // For other errors, return 500
                return new ResponseEntity<>("Failed to delete user address.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error saving user addres: " + e.getMessage());
            return new ResponseEntity<>("Error deleting user address: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete user
    /*
       This functionality won't be used any more
    @DeleteMapping("/delete")
    */
    public ResponseEntity<?> deleteUser(){
        try{
            userService.deleteUser();
            return new ResponseEntity<>("User deleted successfully",HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting user: " + e.getMessage());
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Reset password
    @PutMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestParam String password) {
        try {
            // Call service method to update the user
            int updateStatus = userService.resetPassword(password);

            if (updateStatus == 1) {
                // If update is successful, return 200 OK
                return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
            } else if (updateStatus == -1) {
                // If user is not found, return 404
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            } else {
                // For other errors, return 500
                return new ResponseEntity<>("Failed to reset password.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error updating user: " + e.getMessage());
            return new ResponseEntity<>("Error resetting password: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get user details
    @GetMapping("/getUser")
    public ResponseEntity<?> getUserByUID(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());
            UserResponse user = userRepository.findUserById(existingUser.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>("Error fetching using details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
