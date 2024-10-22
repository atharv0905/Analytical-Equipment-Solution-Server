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

    @PostMapping("/contact")
    public ResponseEntity<?> sendMail(@RequestBody EmailRequest emailRequest){
        try{
            String url = emailService.sendDraftMail(emailRequest.getSubject(), emailRequest.getBody());
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while sending email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
}
