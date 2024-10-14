/**
 * File: CartDetailsResponse.java
 * Author: Atharv Mirgal
 * Description: This class serves as a data transfer object (DTO) for transferring cart item details
 *              from the server to the client in response to cart-related API requests. It includes
 *              fields such as product ID, item ID, product name, product image, price, and quantity,
 *              offering a structured way to represent cart items. The class is annotated with
 *              Lombok annotations for automatic generation of getters, setters, constructors, and
 *              provides a convenient way to handle cart details in the application's business logic.
 * Created on: 14/10/2024
 * Last Modified: 14/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailsResponse {

    private String product_id;

    private String item_id;

    private String product_name;

    private String product_image;

    private Long price;

    private Long quantity;
}
