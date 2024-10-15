/**
 * File: OrderHistoryResponse.java
 * Author: Atharv Mirgal
 * Description: This class serves as a response object for representing the order history details,
 *              including fields such as order_id, product_id, product_name, product_price,
 *              quantity, product_image, and order_date. It utilizes Lombok annotations to
 *              automatically generate boilerplate code like getters, setters, and constructors.
 *              This class is designed to be used in API responses, providing a structured format
 *              for order history data.
 * Created on: 15/10/2024
 * Last Modified: 15/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {

    private String order_id;

    private String product_id;

    private String product_name;

    private String product_price;

    private Long quantity;

    private String product_image;

    private String order_date;

}
