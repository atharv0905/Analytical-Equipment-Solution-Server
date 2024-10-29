/**
 * File: ProductSummaryResponse.java
 * Author: Atharv Mirgal
 * Description: This response class represents a summary of product data, containing the product ID and total
 *              quantity for the specified product. Commonly used in analytics and reporting, it provides a
 *              streamlined structure for summarizing product-related metrics in response payloads. The class
 *              uses Lombok annotations to generate constructors, getters, and setters, simplifying initialization
 *              and enhancing readability.
 * Created on: 29/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryResponse {

    private String productId;

    private long totalQuantity;
}
