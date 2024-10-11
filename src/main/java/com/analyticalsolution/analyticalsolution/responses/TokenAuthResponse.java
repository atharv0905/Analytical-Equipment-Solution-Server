/**
 * File: TokenAuthResponse.java
 * Author: Atharv Mirgal
 * Description: A response class representing the result of token authentication.
 *              It includes the user details and the status of the authentication.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import com.analyticalsolution.analyticalsolution.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenAuthResponse {

    private User user;

    private boolean status;

    public TokenAuthResponse(Boolean status){
        this.status = status;
    }
}
