package com.analyticalsolution.analyticalsolution.requests;

import com.analyticalsolution.analyticalsolution.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineCheckoutRequest {

    private Sale sale;

    private String customer_id;

    private Boolean isNewAddress;

}
