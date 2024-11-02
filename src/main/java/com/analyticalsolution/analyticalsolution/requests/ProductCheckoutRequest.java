package com.analyticalsolution.analyticalsolution.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCheckoutRequest {

    private String product_id;

    private Long product_price;

    private Long product_profit;

    private Long product_quantity;

}
