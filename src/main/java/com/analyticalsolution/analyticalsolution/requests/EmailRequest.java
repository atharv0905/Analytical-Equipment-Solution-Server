/**
 * File: EmailRequest.java
 * Author: Atharv Mirgal
 * Description: This class represents a request object for sending emails, encapsulating the necessary
 *              details such as the email's subject and body. It uses Lombok annotations to generate
 *              boilerplate code like getters, setters, constructors, and toString method.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String subject;

    private String body;
}
