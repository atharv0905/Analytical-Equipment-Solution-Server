/**
 * File: AnalysisController.java
 * Author: Atharv Mirgal
 * Description: This controller class manages endpoints related to sales analysis, providing methods for
 *              retrieving insights on revenue and profit metrics. It leverages the AnalysisService to handle
 *              business logic and interacts with the front-end through RESTful API endpoints. The controller
 *              includes methods for calculating and fetching monthly revenue and profit data, responding with
 *              detailed insights. It handles exceptions gracefully, providing appropriate HTTP responses for
 *              error handling and ensuring smooth integration with client applications.
 * Created on: 28/10/2024
 * Last Modified: 30/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.responses.*;
import com.analyticalsolution.analyticalsolution.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    // Calculate monthly revenue and profit
    @GetMapping("/monthly-sales")
    public ResponseEntity<?> getMonthlyRevenueProfit(){
        try{
            String tableName = "orders";
            List<RevenueProfitResponse> revenueProfitResponses = analysisService.calculateMonthlyRevenueAndProfit(tableName);
            return new ResponseEntity<>(revenueProfitResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get top sellers
    @GetMapping("/top-sellers")
    public ResponseEntity<?> getTopSellersForDash(){
        try{
            List<TopSellerResponse> topSellers = analysisService.getTopSellers();
            return new ResponseEntity<>(topSellers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get online vs offline revenue & profit
    @GetMapping("/online-offline-sales")
    public ResponseEntity<?> getOnlineOfflineSales(){
        try{
            String offlineSales = "offline_order_summary";
            String onlineSales = "online_order_summary";
            List<RevenueProfitResponse> offlineRevenueProfit = analysisService.calculateMonthlyRevenueAndProfit(offlineSales);
            List<RevenueProfitResponse> onlineRevenueProfit = analysisService.calculateMonthlyRevenueAndProfit(onlineSales);

            OnlineOfflineSalesResponse revenueProfitResponses = new OnlineOfflineSalesResponse(offlineRevenueProfit, onlineRevenueProfit);
            return new ResponseEntity<>(revenueProfitResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Fetching pending and completed order count
    @GetMapping("/pending-complete-order-count")
    public ResponseEntity<?> getPendingCompletedOrderCount(){
        try {
            OrderStatusResponse pendingCompletedOrderCount = analysisService.getPendingCompletedOrderCount();
            return new ResponseEntity<>(pendingCompletedOrderCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get count of repeating customers
    /* This function is furthur merged in quick stats
    @GetMapping("/page-reach")
     */
    public ResponseEntity<?> getPageReach(){
        try{
            Long pageReach = analysisService.getPageReach();
            return new ResponseEntity<>(pageReach, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void savePageReachOnInterval(){
        System.out.println("Updated page reach");
        analysisService.updatePageReach();
    }

    // Get count of repeating customers
    /* This function is furthur merged in quick stats
    @GetMapping("/returning-customers")
     */
    public ResponseEntity<?> getCountOfRepeatingCustomers(){
        try{
            Long countOfRepeatingCustomers = analysisService.getCountOfRepeatingCustomers();
            return new ResponseEntity<>(countOfRepeatingCustomers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get count of repeating customers
    /* This function is furthur merged in quick stats
    @GetMapping("/conversion-rate")
    */
    public ResponseEntity<?> getConversionRate(){
        try{
            Long conversionRate = analysisService.getConversionRate();
            return new ResponseEntity<>(conversionRate, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/quick-stats")
    public ResponseEntity<?> getQuickStats(){
        try{
            Long pageReach = analysisService.getPageReach();
            Long countOfRepeatingCustomers = analysisService.getCountOfRepeatingCustomers();
            Long conversionRate = analysisService.getConversionRate();

            QuickStatsResponse quickStatsResponse = new QuickStatsResponse(pageReach, countOfRepeatingCustomers, conversionRate);
            return new ResponseEntity<>(quickStatsResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
