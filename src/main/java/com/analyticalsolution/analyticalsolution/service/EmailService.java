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
import com.analyticalsolution.analyticalsolution.requests.EmailVerificationRequest;
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

    public void sendVerificationMail(String email){
        try {
            System.out.println(email);

            String token = jwtUtils.generateVerificationToken(email);

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

            String checkUsersSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            String checkEmailVerificationSql = "SELECT COUNT(*) FROM email_verification WHERE email = ?";
            String deleteEmailVerificationSql = "DELETE FROM email_verification WHERE email = ?";
            String insertEmailVerificationSql = "INSERT INTO email_verification(email, verified) VALUES(?, ?)";

            int userCount = jdbcTemplate.queryForObject(checkUsersSql, new Object[]{email}, Integer.class);

            if (userCount == 0) {
                int verificationCount = jdbcTemplate.queryForObject(checkEmailVerificationSql, new Object[]{email}, Integer.class);

                if (verificationCount > 0) {
                    jdbcTemplate.update(deleteEmailVerificationSql, email);
                }

                jdbcTemplate.update(insertEmailVerificationSql, email, false);
            }

            javaMailSender.send(messagePreparator);
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
        }
    }

    public Boolean verifyEmail(EmailVerificationRequest emailVerificationRequest){
        try {
            Boolean isValid = jwtUtils.validateToken(emailVerificationRequest.getToken());
            if(isValid){
                System.out.println("Email verified");
                String sql = "UPDATE email_verification SET verified = ? WHERE email = ?";
                jdbcTemplate.update(sql, true, emailVerificationRequest.getEmail());
                return true;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean sendPasswordResetMail(String email) {
        try {
            System.out.println(email);

            // Query to find the username associated with the email
            String getUserSql = "SELECT username FROM users WHERE email = ?";
            String username = jdbcTemplate.queryForObject(getUserSql, new Object[]{email}, String.class);

            if (username != null) {
                String token = jwtUtils.generatePasswordToken(username); // Pass username instead of email

                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                    messageHelper.setFrom("atharvmirgal09@gmail.com");
                    messageHelper.setTo(email);
                    messageHelper.setSubject("Reset Password");

                    // Define HTML content for the email body
                    String body = "<h1>Reset Password</h1>" +
                            "<p>Please click the link below to reset your account password:</p>" +
                            "<a href='" + "http://localhost:5501/user/view/email-verify.html?token=" + token + "' style='display: inline-block; padding: 10px 20px; " +
                            "background-color: #1a73e8; color: white; text-decoration: none; border-radius: 5px;'>Reset Password</a>";

                    messageHelper.setText(body, true); // Set to 'true' for HTML content
                };

                String checkUsersSql = "SELECT COUNT(*) FROM users WHERE email = ?";
                int userCount = jdbcTemplate.queryForObject(checkUsersSql, new Object[]{email}, Integer.class);

                if (userCount > 0) {
                    javaMailSender.send(messagePreparator);
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
            return false;
        }
    }

}
