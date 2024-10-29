package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private String sale_id;

    private String customer_name;

    private String customer_email;

    private Long customer_phone;

    private String shipping_address;

    private String company_name;

    private String company_email;

    private String company_phone;

    private String company_address;

    private Long gst_cost;

    private Long shipping_cost;

    private Long total_cost;

    private Date order_date;

    private List<ProductInvoiceResponse> products = new ArrayList<>();

}
