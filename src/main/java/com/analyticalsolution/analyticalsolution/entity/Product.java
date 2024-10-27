/**
 * File: User.java
 * Author: Atharv Mirgal
 * Description: This entity class represents the User object with fields such as id, username,
 *              name, password, email, phone, addresses, and roles. It utilizes Lombok annotations
 *              for generating boilerplate code like getters, setters, constructors, and more.
 *              The class is designed for use with a MongoDB database, using the `@Id` annotation
 *              for the primary key.
 * Created on: 12/10/2024
 * Last Modified: 27/10/2024
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
public class Product {

    @Id
    @NonNull
    private String product_id;

    @NonNull
    private String product_name;

    @NonNull
    private String product_desc;

    @NonNull
    private String product_category;

    @NonNull
    private String estimated_delivery_time;

    @NonNull
    private Long product_price;

    @NonNull
    private Integer product_status;

    @NonNull
    private ArrayList<String> product_images = new ArrayList<>();

}
