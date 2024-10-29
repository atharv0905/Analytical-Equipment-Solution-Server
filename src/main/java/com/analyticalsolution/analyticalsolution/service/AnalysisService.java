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
import com.analyticalsolution.analyticalsolution.responses.*;
import com.analyticalsolution.analyticalsolution.utils.AnalysisUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AnalysisService {

    @Autowired
    private AnalysisUtils analysisUtils;

    @Autowired
    private ProductService productService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String BASE_URL = "http://localhost:3000/";

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

    // Get top sellers
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

    // Get new arrivals
    public List<FetchProductsResponse> listAllProductsOrderedByCreation() {
        List<FetchProductsResponse> productList = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, new RowMapper<FetchProductsResponse>() {
            @Override
            public FetchProductsResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                FetchProductsResponse product = new FetchProductsResponse();
                product.setProduct_id(rs.getString("product_id"));
                product.setProduct_name(rs.getString("product_name"));
                String productImagesJson = rs.getString("product_images");
                JSONArray jsonArray = new JSONArray(productImagesJson);
                String firstImageUrl = jsonArray.length() > 0 ? jsonArray.getString(0) : null;
                firstImageUrl = BASE_URL + firstImageUrl;
                product.setProduct_image(firstImageUrl);
                return product;
            }
        });
    }

}
