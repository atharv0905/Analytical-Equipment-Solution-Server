/**
 * File: UserAddress.java
 * Author: Atharv Mirgal
 * Description: This DTO class represents the UserAddress object with fields such as id, customer_id, and address.
 *              It utilizes Lombok annotations for generating boilerplate code like getters, setters, constructors,
 *              and other methods.
 * Created on: 28/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @NonNull
    private String id;

    @NonNull
    private String customer_id;

    @NonNull
    private String address;
}
