/**
 * File: AnalysisUtils.java
 * Author: Atharv Mirgal
 * Description: This utility class provides helper methods for processing and analyzing order data, including
 *              fetching orders within a specified date range and generating monthly summaries of product sales.
 *              It interacts with the OrderRepository to retrieve data and performs grouping and aggregation
 *              operations using Java streams. The class builds structured responses such as OrderFetchResponse
 *              and product summaries to facilitate analytics. Its methods streamline the analysis workflow,
 *              enabling efficient reporting and insights into sales performance on a monthly basis.
 * Created on: 28/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.entity.Order;
import com.analyticalsolution.analyticalsolution.repository.OrderRepository;
import com.analyticalsolution.analyticalsolution.responses.DateRange;
import com.analyticalsolution.analyticalsolution.responses.OrderFetchResponse;
import com.analyticalsolution.analyticalsolution.responses.ProductSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalysisUtils {

    @Autowired
    private OrderRepository orderRepository;

    // Fetch all the orders in date range
    public List<Order> getOrdersByRange(Date startDate, Date endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }

    // Get all the orders months wise
    public List<OrderFetchResponse> getOrders() {
        List<Order> allOrders = orderRepository.findAll();

        // Group orders by year and month
        Map<String, List<Order>> groupedOrders = allOrders.stream()
                .collect(Collectors.groupingBy(order -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(order.getOrderDate());
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1; // Months are 0-based
                    return year + "-" + month;
                }));

        // Build a list of OrderFetchResponse, one per month
        List<OrderFetchResponse> responses = new ArrayList<>();
        for (Map.Entry<String, List<Order>> entry : groupedOrders.entrySet()) {
            String[] yearMonth = entry.getKey().split("-");
            int year = Integer.parseInt(yearMonth[0]);
            int month = Integer.parseInt(yearMonth[1]) - 1; // Months are 0-based

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);

            // Set start date to the first day of the month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();

            // Set end date to the last day of the month
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            // Create OrderFetchResponse for each month
            OrderFetchResponse response = new OrderFetchResponse();
            response.setStart_date(startDate);
            response.setEnd_date(endDate);
            response.setOrderList(entry.getValue());
            responses.add(response);
        }

        return responses;
    }

    // Get summary of units sold of each product month wise
    public List<Map<DateRange, List<ProductSummaryResponse>>> getMonthlyProductSummaries() {
        List<Map<DateRange, List<ProductSummaryResponse>>> monthlyProductSummaries = new ArrayList<>();
        List<OrderFetchResponse> monthlyOrders = getOrders();
        for (OrderFetchResponse monthlyOrder : monthlyOrders) {
            // Group by product_id and sum quantities for each product
            Map<String, Long> productQuantityMap = monthlyOrder.getOrderList().stream()
                    .collect(Collectors.groupingBy(Order::getProduct_id, Collectors.summingLong(Order::getQuantity)));

            // Create a list of ProductSummaryResponse for the current month
            List<ProductSummaryResponse> productSummaries = productQuantityMap.entrySet().stream()
                    .map(entry -> new ProductSummaryResponse(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            // Prepare the date range and add it to the response
            DateRange dateRange = new DateRange(monthlyOrder.getStart_date(), monthlyOrder.getEnd_date());
            Map<DateRange, List<ProductSummaryResponse>> monthlySummary = Map.of(dateRange, productSummaries);

            monthlyProductSummaries.add(monthlySummary);
        }

        return monthlyProductSummaries;
    }
}
