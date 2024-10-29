package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInvoiceResponse {

    private String product_name;

    private Long product_price;

    private Long quantity;

    private Long total_price;
}
