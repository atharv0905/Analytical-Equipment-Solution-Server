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
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.responses.RevenueProfitResponse;
import com.analyticalsolution.analyticalsolution.responses.TopSellerResponse;
import com.analyticalsolution.analyticalsolution.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            List<RevenueProfitResponse> revenueProfitResponses = analysisService.calculateMonthlyRevenueAndProfit();
            return new ResponseEntity<>(revenueProfitResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top-sellers")
    public ResponseEntity<?> getTopSellers(){
        try{
            List<TopSellerResponse> topSellers = analysisService.getTopSellers();

            List<TopSellerResponse> limitedTopSellers = topSellers.stream()
                    .limit(5)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(limitedTopSellers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}