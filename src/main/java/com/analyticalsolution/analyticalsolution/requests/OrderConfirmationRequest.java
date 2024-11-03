package com.analyticalsolution.analyticalsolution.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmationRequest {

    private String sale_id;

    private String status;

}
