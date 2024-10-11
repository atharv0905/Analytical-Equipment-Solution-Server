package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @NonNull
    private String id;

    @NonNull
    private String username;

    @NonNull
    private String name;

    @NonNull
    private String password;

    @NonNull
    private String email;

    @NonNull
    private Long phone;

    private ArrayList<String> addresses = new ArrayList<>();

    @NonNull
    private ArrayList<String> roles = new ArrayList<>();

}
