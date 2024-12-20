package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopSellerResponse {

    private String product_id;

    private String product_image;

    private String product_name;

    private Long quantity;
}
