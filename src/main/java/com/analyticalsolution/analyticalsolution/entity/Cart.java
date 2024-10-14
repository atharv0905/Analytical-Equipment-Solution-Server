/**
 * File: Cart.java
 * Author: Atharv Mirgal
 * Description: This entity class represents the Cart object with fields such as cart_id and item_ids.
 *              It utilizes Lombok annotations for generating boilerplate code like getters, setters,
 *              constructors, and more. The class is designed for use with a MongoDB database, using the
 *              `@Id` annotation for the primary key.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

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
public class Cart {

    @Id
    @NonNull
    private String cart_id;

    @NonNull
    private String customer_id;

    @NonNull
    private ArrayList<String> item_ids = new ArrayList<>();
}
