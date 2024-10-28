package com.analyticalsolution.analyticalsolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @NonNull
    private String id;

    @NonNull
    private String customer_id;

    @NonNull
    private String address;
}
