package com.analyticalsolution.analyticalsolution.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping("/create-order/{data}")
    public ResponseEntity<?> createOrder(@PathVariable Integer data) throws Exception {

        int amt = data;
        System.out.println("Amount: " + amt);
        // Initialize Razorpay client with your key and secret
        RazorpayClient razorpay = new RazorpayClient("rzp_test_hD75gZIHGX2XGb", "y0ZZRJJa2QUwe9c8w2VUqUeG");

        // Create a JSON object for the order request
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amt * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt#1");


        try {
            // Create the order using RazorpayClient instance
            Order order = razorpay.orders.create(orderRequest);

            // Return the order details as a JSON string
            return new ResponseEntity<>(order.toString(), HttpStatus.OK);
        } catch (RazorpayException e) {
            e.printStackTrace(); // Handle Razorpay exception
            return new ResponseEntity<>("Unable to create order", HttpStatus.BAD_REQUEST);
        }
    }
}
