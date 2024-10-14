/**
 * File: FetchProductsResponse.java
 * Author: Atharv Mirgal
 * Description: This class serves as a response model for fetching product details. It encapsulates the basic information
 *              of a product, including its ID, name, and image URL, which are essential for displaying product data
 *              in a user-friendly format. Utilizes Lombok annotations for boilerplate code reduction, providing constructors
 *              for easy instantiation and getters/setters for access. It is designed to be used in product retrieval
 *              API responses for seamless client-server communication.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchProductsResponse {

    private String product_id;

    private String product_name;

    private String product_desc;

    private String product_category;

    private String product_image;
}
