/**
 * File: UserController.java
 * Author: Atharv Mirgal
 * Description: This controller manages user-related operations such as updating user details.
 *              It provides an endpoint to update user information, and handles different response statuses
 *              based on the success or failure of the update operation. Logs errors for debugging purposes.
 * Created on: 11/10/2024
 * Last Modified: 14/10/2024
 */

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

    // Delete user
    @DeleteMapping("/delete")
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
}
