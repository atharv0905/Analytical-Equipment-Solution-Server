package com.analyticalsolution.analyticalsolution.requests;

import com.analyticalsolution.analyticalsolution.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    private Sale sale;

    private Boolean isNewAddress;

}
