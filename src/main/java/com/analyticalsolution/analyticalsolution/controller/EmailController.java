/**
 * File: EmailController.java
 * Author: Atharv Mirgal
 * Description: This controller handles email-related API requests. It provides an endpoint to send
 *              emails with a specified subject and body through the EmailService.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.requests.EmailRequest;
import com.analyticalsolution.analyticalsolution.service.EmailService;
import com.analyticalsolution.analyticalsolution.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/verification")
    public ResponseEntity<?> sendVerificationMail(){
        try{
            emailService.sendVerificationMail();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while sending email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(){
        try{
            Boolean isValid = emailService.verifyEmail();
            return new ResponseEntity<>(isValid, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while verifying email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
