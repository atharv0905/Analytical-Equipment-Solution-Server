/**
 * File: LoginRequest.java
 * Author: Atharv Mirgal
 * Description: A DTO (Data Transfer Object) class representing the login request payload
 *              containing the username and password fields.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */

package com.analyticalsolution.analyticalsolution.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
