package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResponse {

    private Long pending;

    private Long completed;

}
