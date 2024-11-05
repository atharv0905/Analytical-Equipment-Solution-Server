/**
 * File: EmailService.java
 * Author: Atharv Mirgal
 * Description: This service handles email operations, including generating draft URLs for emails, sending verification and
 *              password reset emails, and verifying email tokens. The service uses Gmail's web interface to create pre-filled
 *              draft emails with encoded subjects and bodies. Verification tokens are generated using JWT and stored in the database
 *              for validation purposes. The service interacts with the database to ensure email uniqueness and to track email verification status.
 * Created on: 13/10/2024
 * Last Modified: 28/10/2024
 */


package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.requests.EmailVerificationRequest;
import com.analyticalsolution.analyticalsolution.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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

//    @Value("${app.base-url}")
    private String BASE_URL = "http://localhost:3000/";

//    @Value("${app.sentTo}")
    private String sentTo = "atharvmirgal09@gmail.com";

    // Send contact email
    @Transactional
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    // Send verification mail
    @Transactional
    public void sendVerificationMail(String email){
        try {
            System.out.println(email);

            String token = jwtUtils.generateVerificationToken(email);

            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setFrom("atharvmirgal09@gmail.com");
                messageHelper.setTo(email);
                messageHelper.setSubject("Email Verification Required");

                // Enhanced HTML content for email verification
                String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h1 style='color: #1a73e8; text-align: center;'>Verify Your Email Address</h1>" +
                        "<p style='font-size: 16px;'>Hello,</p>" +
                        "<p style='font-size: 15px;'>Thank you for signing up! To complete your registration, please verify your email address by clicking the button below:</p>" +
                        "<p style='text-align: center; margin: 20px 0;'>" +
                        "<a href='" + "http://localhost:5501/user/view/email-verify.html?token=" + token + "' style='display: inline-block; padding: 12px 25px; background-color: #1a73e8; color: white; font-size: 16px; text-decoration: none; border-radius: 5px;'>Verify Email</a>" +
                        "</p>" +
                        "<p style='font-size: 14px; color: #555;'>If you did not create an account, please disregard this email.</p>" +
                        "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                        "<p style='text-align: center; font-size: 12px; color: #999;'>This email was sent by Analytical Equipments Solutions. If you have any questions, please contact support.</p>" +
                        "</div>";

                messageHelper.setText(body, true); // Set to 'true' for HTML content
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    // Verify email
    @Transactional
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    // Send password reset mail
    @Transactional
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
                    messageHelper.setSubject("Reset Your Password");

                    // Beautified HTML content for the reset password email
                    String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                            "<h1 style='color: #1a73e8; text-align: center;'>Password Reset Request</h1>" +
                            "<p style='font-size: 16px;'>Hello,</p>" +
                            "<p style='font-size: 15px;'>We received a request to reset your account password. To proceed, please click the button below:</p>" +
                            "<p style='text-align: center; margin: 20px 20px;'>" +
                            "<a href='" + "http://localhost:5501/user/view/reset-password.html?token=" + token + "' style='display: inline-block; padding: 12px 25px; background-color: #1a73e8; color: white; font-size: 16px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                            "</p>" +
                            "<p style='font-size: 14px; color: #555;'>If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>" +
                            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                            "<p style='text-align: center; font-size: 12px; color: #999;'>This email was sent by Analytical Equipments Solutions. For your security, this link will expire in 5 minutes.</p>" +
                            "</div>";

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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    // Send password reset mail
    @Transactional
    public Boolean sendOrderConfirmationMail(String customer_id) {
        try {

            // Query to find the username associated with the email
            String getUserSql = "SELECT email FROM users WHERE id = ?";
            String email = jdbcTemplate.queryForObject(getUserSql, new Object[]{customer_id}, String.class);
            System.out.println(email);
            if (email != null) {
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                    messageHelper.setFrom("atharvmirgal09@gmail.com");
                    messageHelper.setTo(email);
                    messageHelper.setSubject("Order Confirmation: Your Request Has Been Accepted");

                    // Enhanced HTML content for order acceptance
                    String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                            "<h1 style='color: #4CAF50; text-align: center;'>🛒 Order Accepted!</h1>" +
                            "<p style='font-size: 16px;'>Great news! Your order request has been <strong>accepted</strong> and is now being processed.</p>" +
                            "<p style='font-size: 15px;'>We are committed to delivering your order promptly and ensuring a smooth experience. You can check your order status by logging into your account.</p>" +
                            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                            "<p style='font-size: 14px; color: #555;'>Thank you for choosing us for your purchase. We value your trust and are excited to serve you!</p>" +
                            "<p style='text-align: center; font-size: 14px; color: #999;'>Feel free to contact us if you have any questions.</p>" +
                            "</div>";

                    messageHelper.setText(body, true); // Set to 'true' for HTML content
                };

                javaMailSender.send(messagePreparator);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    // Send order delivered mail
    @Transactional
    public Boolean sendOrderDeliveredMail(String customer_id, String sale_id) {
        try {
            System.out.println("Sending order status mail...");
            // Query to find the username associated with the email
            String getUserSql = "SELECT email FROM users WHERE id = ?";
            String email = jdbcTemplate.queryForObject(getUserSql, new Object[]{customer_id}, String.class);
            System.out.println(email);
            if (email != null) {
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                    messageHelper.setFrom("atharvmirgal09@gmail.com");
                    messageHelper.setTo(email);
                    messageHelper.setSubject("Order Delivered Successfully");

                    // Enhanced HTML content for order delivery confirmation with invoice download button
                    String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                            "<h1 style='color: #4CAF50; text-align: center;'>Order Delivered!</h1>" +
                            "<p style='font-size: 16px;'>Hello,</p>" +
                            "<p style='font-size: 15px;'>We are pleased to inform you that your order has been <strong>successfully delivered</strong> to your specified address.</p>" +
                            "<p style='font-size: 15px;'>We hope you enjoy your purchase and that it meets your expectations. To download your invoice, please click the button below:</p>" +
                            "<p style='text-align: center; margin: 20px 0;'>" +
                            "<a href='http://localhost:5501/user/view/invoice.html?saleId=" + sale_id + "' style='display: inline-block; padding: 12px 25px; background-color: #1a73e8; color: white; font-size: 16px; text-decoration: none; border-radius: 5px;'>Download Invoice</a>" +
                            "</p>" +
                            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                            "<p style='text-align: center; font-size: 14px; color: #555;'>Thank you for choosing Analytical Equipments Solutions! We look forward to serving you again.</p>" +
                            "<p style='text-align: center; font-size: 12px; color: #999;'>If you have any questions, please contact our support team.</p>" +
                            "</div>";

                    messageHelper.setText(body, true); // Set to 'true' for HTML content
                };


                javaMailSender.send(messagePreparator);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    // Send order dispatched mail
    @Transactional
    public Boolean sendOrderDispatchedMail(String customer_id, String sale_id) {
        try {
            System.out.println("Sending order status mail...");
            // Query to find the username associated with the email
            String getUserSql = "SELECT email FROM users WHERE id = ?";
            String email = jdbcTemplate.queryForObject(getUserSql, new Object[]{customer_id}, String.class);
            System.out.println(email);
            if (email != null) {
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                    messageHelper.setFrom("atharvmirgal09@gmail.com");
                    messageHelper.setTo(email);
                    messageHelper.setSubject("Order Dispatched Successfully");

                    // Enhanced HTML content for order dispatch notification with invoice download button
                    String body = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                            "<h1 style='color: #1a73e8; text-align: center;'>Your Order is on its Way!</h1>" +
                            "<p style='font-size: 16px;'>Hello,</p>" +
                            "<p style='font-size: 15px;'>We are excited to inform you that your order has been <strong>successfully dispatched</strong> and is on its way to you!</p>" +
                            "<p style='font-size: 15px;'>We hope you will enjoy your purchase. To download your invoice, please click the button below:</p>" +
                            "<p style='text-align: center; margin: 20px 0;'>" +
                            "<a href='http://localhost:5501/user/view/invoice.html?saleId=" + sale_id + "' style='display: inline-block; padding: 12px 25px; background-color: #1a73e8; color: white; font-size: 16px; text-decoration: none; border-radius: 5px;'>Download Invoice</a>" +
                            "</p>" +
                            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                            "<p style='text-align: center; font-size: 14px; color: #555;'>Thank you for choosing Analytical Equipments Solutions! We look forward to serving you again.</p>" +
                            "<p style='text-align: center; font-size: 12px; color: #999;'>If you have any questions, please contact our support team.</p>" +
                            "</div>";

                    messageHelper.setText(body, true); // Set to 'true' for HTML content
                };

                javaMailSender.send(messagePreparator);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Exception while sending email: " + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

}
