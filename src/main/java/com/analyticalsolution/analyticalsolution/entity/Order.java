/**
 * File: Order.java
 * Author: Atharv Mirgal
 * Description: This entity class represents the Order object with fields such as order_id, product_id, and quantity.
 *              It utilizes Lombok annotations for generating boilerplate code like getters, setters,
 *              constructors, and more. The class is designed for use with a database, using the `@Id` annotation
 *              for the primary key.
 * Created on: 15/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @NonNull
    private String order_id;

    @NonNull
    private String product_id;

    @NonNull
    private long quantity;

    private Date orderDate;
}
