package com.analyticalsolution.analyticalsolution.responses;

import com.analyticalsolution.analyticalsolution.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;

    private User user;
}
