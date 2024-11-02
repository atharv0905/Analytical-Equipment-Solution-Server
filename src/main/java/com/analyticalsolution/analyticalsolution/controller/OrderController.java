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
import com.analyticalsolution.analyticalsolution.requests.CheckoutRequest;
import com.analyticalsolution.analyticalsolution.requests.OfflineCheckoutRequest;
import com.analyticalsolution.analyticalsolution.responses.InvoiceResponse;
import com.analyticalsolution.analyticalsolution.responses.OrderHistoryResponse;
import com.analyticalsolution.analyticalsolution.service.OrderService;
import com.analyticalsolution.analyticalsolution.service.UserService;
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
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutRequest){
        try{
            int checkout = orderService.checkout(checkoutRequest);
            if(checkout == 1){
                return new ResponseEntity<>("Order placed", HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Order not placed", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error occured while placing order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Offline order checkout
    @PostMapping("/offline-checkout")
    public ResponseEntity<?> offlineCheckout(@RequestBody OfflineCheckoutRequest checkoutRequest){
        try{
            int checkout = orderService.offlineCheckout(checkoutRequest);
            if(checkout == 1){
                return new ResponseEntity<>("Order placed", HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Order not placed", HttpStatus.BAD_REQUEST);
            }
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

    // Get invoice details
    @PostMapping("/generate-invoice")
    public ResponseEntity<?> generateInvoice(@RequestParam String saleID){
        try{
            InvoiceResponse invoiceResponse = orderService.generateInvoice(saleID);
            return new ResponseEntity<>(invoiceResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
