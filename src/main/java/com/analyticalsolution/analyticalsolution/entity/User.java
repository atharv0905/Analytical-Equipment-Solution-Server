/**
 * File: User.java
 * Author: Atharv Mirgal
 * Description: This entity class represents the User object with fields such as id, username,
 *              name, password, email, phone, addresses, and roles. It utilizes Lombok annotations
 *              for generating boilerplate code like getters, setters, constructors, and more.
 *              The class is designed for use with a MongoDB database, using the `@Id` annotation
 *              for the primary key.
 * Created on: 11/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> addresses = new ArrayList<>();

    @NonNull
    private ArrayList<String> roles = new ArrayList<>();

}
