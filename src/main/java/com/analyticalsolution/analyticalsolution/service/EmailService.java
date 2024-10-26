/**
 * File: EmailService.java
 * Author: Atharv Mirgal
 * Description: This service provides functionality for creating a URL to send emails using Gmail's web interface.
 *              It takes an email subject and body, encodes them, and generates a pre-filled email draft URL.
 * Created on: 13/10/2024
 * Last Modified: 16/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String BASE_URL = "http://localhost:3000/";

    private static final String sentTo = "atharvmirgal09@gmail.com";

    public String sendDraftMail(String subject, String body){
        System.out.println("mail request hit");
        try{
            String encodedSubject = UriUtils.encode(subject, StandardCharsets.UTF_8);
            String encodedBody = UriUtils.encode(body, StandardCharsets.UTF_8);

            return String.format(
                    "https://mail.google.com/mail/?view=cm&fs=1&to=%s&su=%s&body=%s",
                    sentTo,
                    encodedSubject,
                    encodedBody
            );
        } catch (Exception e) {
            log.error("Error while sending mail: " + e.getMessage());
            return null;
        }
    }

    public void sendVerificationMail(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName().toString());
            if (existingUser == null) {
                log.error("User not found.");
            }

            String email = existingUser.getEmail();
            System.out.println(email);
            String username = existingUser.getUsername();

            String token = jwtUtils.generateVerificationToken(username);

            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setFrom("atharvmirgal09@gmail.com");
                messageHelper.setTo(email);
                messageHelper.setSubject("Email Verification");

                // Define HTML content for the email body
                String body = "<h1>Email Verification</h1>" +
                        "<p>Please click the link below to verify your email address:</p>" +
                        "<a href='" + "http://localhost:5501/user/view/email-verify.html?token=" + token + "' style='display: inline-block; padding: 10px 20px; " +
                        "background-color: #1a73e8; color: white; text-decoration: none; border-radius: 5px;'>Verify Email</a>";

                messageHelper.setText(body, true); // Set the second parameter to 'true' for HTML content
            };

            javaMailSender.send(messagePreparator);
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
        }
    }

    public Boolean verifyEmail(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName().toString());
            if(existingUser != null){
                String sql = "UPDATE users SET verified = ? WHERE username = ?";
                jdbcTemplate.update(sql, true, existingUser.getUsername());
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
