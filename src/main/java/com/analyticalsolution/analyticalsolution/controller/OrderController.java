/**
 * File: OrderController.java
 * Author: Atharv Mirgal
 * Description: This controller class handles incoming HTTP requests related to orders, providing
 *              endpoints for cart checkout and retrieving order history. It uses the `OrderService`
 *              to process business logic and manage order data. The class is annotated with
 *              `@RestController` to designate it as a Spring MVC controller and `@RequestMapping`
 *              to define the base URL for order-related operations. It includes endpoints for
 *              placing orders and fetching the user's order history, returning appropriate HTTP
 *              responses based on the operation's success or failure.
 * Created on: 15/10/2024
 * Last Modified: 15/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.Sale;
import com.analyticalsolution.analyticalsolution.responses.OrderHistoryResponse;
import com.analyticalsolution.analyticalsolution.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Cart checkout
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Sale sale){
        try{
            orderService.checkout(sale);
            return new ResponseEntity<>("Order placed", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occured while placing order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order-history")
    public ResponseEntity<?> getOrderHistory(){
        try{
            List<OrderHistoryResponse> orderHistoryByUser = orderService.getOrderHistoryByUser();
            return new ResponseEntity<>(orderHistoryByUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occured while fetching order history", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
