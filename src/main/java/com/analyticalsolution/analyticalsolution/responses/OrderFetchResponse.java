/**
 * File: OrderFetchResponse.java
 * Author: Atharv Mirgal
 * Description: This response class encapsulates order data within a specified date range, including a list of
 *              Order entities and the start and end dates of the range. Primarily used for conveying order
 *              details in response payloads, it allows for convenient packaging of filtered order data. Lombok
 *              annotations simplify code by generating constructors, getters, and setters, facilitating easy
 *              initialization and data manipulation within the application.
 * Created on: 28/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import com.analyticalsolution.analyticalsolution.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFetchResponse {

    private Date start_date;

    private Date end_date;

    private List<Order> orderList = new ArrayList<>();
}
