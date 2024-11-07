/**
 * File: Sale.java
 * Author: Atharv Mirgal
 * Description: This DTO class represents the Sale object with fields such as sale_id, customer_id, order_ids,
 *              order_confirmation_status, order_status, shipping_address, contact_phone, transaction_id,
 *              payment_status, invoice_number, sale_mode, and order_date.
 *              It utilizes Lombok annotations for generating boilerplate code like getters, setters,
 *              constructors, and more.
 * Created on: 15/10/2024
 * Last Modified: 15/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    @NonNull
    private String sale_id;

    @NonNull
    private String customer_id;

    @NonNull
    private String customer_name;

    @NonNull
    private String order_confirmation_status;

    @NonNull
    private String order_status;

    @NonNull
    private String shipping_address;

    @NonNull
    private long contact_phone;

    @NonNull
    private String transaction_id;

    @NonNull
    private String payment_status;

    @NonNull
    private String order_id;

    @NonNull
    private String sale_mode;

    private Date order_date;

}

