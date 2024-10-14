/**
 * File: CartItem.java
 * Author: Atharv Mirgal
 * Description: This entity class represents the CartItem object with fields such as item_id,
 *              product_id, and quantity. It utilizes Lombok annotations for generating boilerplate code
 *              like getters, setters, constructors, and more. The class is designed for use with
 *              a MongoDB database, using the `@Id` annotation for the primary key.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @NonNull
    private String item_id;

    @NonNull
    private String product_id;

    @NonNull
    private Long quantity;
}
