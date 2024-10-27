/**
 * File: EmailVerificationRequest.java
 * Author: Atharv Mirgal
 * Description: This data transfer object (DTO) is used to encapsulate the request details required for email verification.
 *              It contains fields for the user's email address and a verification token, which is sent to the server
 *              to confirm the user's email. The class includes default and parameterized constructors,
 *              along with getter and setter methods generated by Lombok.
 * Created on: 26/10/2024
 * Last Modified: 26/10/2024
 */

package com.analyticalsolution.analyticalsolution.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {

    private String email;

    private String token;

}