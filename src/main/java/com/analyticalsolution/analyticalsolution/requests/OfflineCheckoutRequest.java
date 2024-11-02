package com.analyticalsolution.analyticalsolution.requests;

import com.analyticalsolution.analyticalsolution.entity.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineCheckoutRequest {

    private Sale sale;

    private String customer_id;

    private Boolean isNewAddress;

    private List<ProductCheckoutRequest> products = new ArrayList<>();

}
