/**
 * File: EmailService.java
 * Author: Atharv Mirgal
 * Description: This service provides functionality for creating a URL to send emails using Gmail's web interface.
 *              It takes an email subject and body, encodes them, and generates a pre-filled email draft URL.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailService {

    private static final String sentTo = "atharvmirgal09@gmail.com";

    public String sendMail(String subject, String body){
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
}
