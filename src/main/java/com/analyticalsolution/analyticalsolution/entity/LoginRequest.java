package com.analyticalsolution.analyticalsolution.entity;

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
