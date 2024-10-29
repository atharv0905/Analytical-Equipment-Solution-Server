/**
 * File: AnalysisService.java
 * Author: Atharv Mirgal
 * Description: This service class handles complex analytical computations, such as calculating monthly revenue
 *              and profit for products, using supporting data from product details and orders. By integrating
 *              with utilities for summarizing product metrics and leveraging the ProductService, it accurately
 *              derives financial metrics for specified date ranges. This service aggregates and processes data
 *              to generate detailed monthly summaries, converting them into a list of RevenueProfitResponse objects
 *              for downstream use. Error handling and logging ensure reliability and traceability of the analysis.
 * Created on: 28/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.responses.DateRange;
import com.analyticalsolution.analyticalsolution.responses.ProductSummaryResponse;
import com.analyticalsolution.analyticalsolution.responses.RevenueProfitResponse;
import com.analyticalsolution.analyticalsolution.responses.TopSellerResponse;
import com.analyticalsolution.analyticalsolution.utils.AnalysisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AnalysisService {

    @Autowired
    private AnalysisUtils analysisUtils;

    @Autowired
    private ProductService productService;

    // Calculates the monthly revenue and profit
    public List<RevenueProfitResponse> calculateMonthlyRevenueAndProfit() {
        List<RevenueProfitResponse> revenueProfitResponses = new ArrayList<>();
        List<Map<DateRange, List<ProductSummaryResponse>>> monthlyProductSummaries = analysisUtils.getMonthlyProductSummaries();
        for (Map<DateRange, List<ProductSummaryResponse>> monthlySummary : monthlyProductSummaries) {
            for (Map.Entry<DateRange, List<ProductSummaryResponse>> entry : monthlySummary.entrySet()) {
                DateRange dateRange = entry.getKey();
                List<ProductSummaryResponse> productSummaries = entry.getValue();

                long totalRevenue = 0;
                long totalProfit = 0;

                for (ProductSummaryResponse productSummary : productSummaries) {
                    String productId = productSummary.getProductId();
                    long quantity = productSummary.getTotalQuantity();

                    Product product = productService.fetchProductById(productId);
                    if (product != null) {
                        // Calculate revenue and profit
                        long revenue = quantity * product.getProduct_price();
                        long profit = quantity * product.getProduct_profit();

                        totalRevenue += revenue;
                        totalProfit += profit;
                    }
                }

                // Create RevenueProfitResponse for the current month
                RevenueProfitResponse revenueProfitResponse = new RevenueProfitResponse();
                revenueProfitResponse.setDate(dateRange.getStartDate());  // Or use an average/representative date if needed
                revenueProfitResponse.setRevenue(totalRevenue);
                revenueProfitResponse.setProfit(totalProfit);

                revenueProfitResponses.add(revenueProfitResponse);
            }
        }

        return revenueProfitResponses;
    }

    public List<TopSellerResponse> getTopSellers() {
        // Create a map to accumulate total quantities for each product
        Map<String, Long> productQuantityMap = new HashMap<>();
        List<Map<DateRange, List<ProductSummaryResponse>>> monthlyProductSummaries = analysisUtils.getMonthlyProductSummaries();

        // Accumulate quantities for each product across all months
        for (Map<DateRange, List<ProductSummaryResponse>> monthlySummary : monthlyProductSummaries) {
            for (Map.Entry<DateRange, List<ProductSummaryResponse>> entry : monthlySummary.entrySet()) {
                List<ProductSummaryResponse> productSummaries = entry.getValue();
                for (ProductSummaryResponse productSummary : productSummaries) {
                    // Merge quantities into the map
                    productQuantityMap.merge(productSummary.getProductId(), productSummary.getTotalQuantity(), Long::sum);
                }
            }
        }

        // Create a list of TopSellerResponse objects
        List<TopSellerResponse> topSellers = productQuantityMap.entrySet().stream()
                .map(entry -> {
                    // Fetch the product details to get the product image
                    Product product = productService.fetchProductById(entry.getKey());
                    String productImage = (product != null && product.getProduct_images() != null && !product.getProduct_images().isEmpty())
                            ? product.getProduct_images().get(0) : ""; // Get the first image if available
                    System.out.println(productImage);

                    return new TopSellerResponse(entry.getKey(), productImage, entry.getValue()); // Use Long directly
                })
                .collect(Collectors.toList());

        // Sort the list in descending order based on quantity
        List<TopSellerResponse> sortedTopSellers = topSellers.stream()
                .sorted(Comparator.comparingLong(TopSellerResponse::getQuantity).reversed())
                .collect(Collectors.toList());

        return sortedTopSellers;
    }

}
