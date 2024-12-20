/**
 * File: PublicController.java
 * Author: Atharv Mirgal
 * Description: This controller manages public-facing API endpoints for user-related actions like authentication, registration,
 *              email verification, and product retrieval. It supports user signup, login, JWT-based token validation, and
 *              email-related operations such as verification and password reset. Product endpoints allow retrieving all products
 *              or specific products by ID. Additionally, it includes testing endpoints to fetch user details by username or user ID.
 *              It utilizes Spring Security for authentication, JWT for secure sessions, and logs errors to aid in debugging.
 * Created on: 11/10/2024
 * Last Modified: 30/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.repository.ProductRepository;
import com.analyticalsolution.analyticalsolution.requests.EmailRequest;
import com.analyticalsolution.analyticalsolution.requests.EmailVerificationRequest;
import com.analyticalsolution.analyticalsolution.requests.LoginRequest;
import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.*;
import com.analyticalsolution.analyticalsolution.service.*;
import com.analyticalsolution.analyticalsolution.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    // Check whether the server is running
    @GetMapping()
    public String serverCheck(){
        return "Server is running...";
    }

    // Handle page reach
    @GetMapping("/hit")
    public ResponseEntity<?> getHit(){
        try{
            analysisService.countPageReach();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Send verification email
    @PostMapping("/verification")
    public ResponseEntity<?> sendVerificationMail(@RequestParam String email){
        try{
            emailService.sendVerificationMail(email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while sending email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Send password reset email
    @PostMapping("/password-reset")
    public ResponseEntity<?> sendPasswordMail(@RequestParam String email){
        try{
            Boolean isMailSent = emailService.sendPasswordResetMail(email);
            if(isMailSent){
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error while sending email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Verify email
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody EmailVerificationRequest emailVerificationRequest){
        try{
            System.out.println("Verify hit");
            Boolean isValid = emailService.verifyEmail(emailVerificationRequest);
            return new ResponseEntity<>(isValid, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while verifying email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create new user
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            int result = userService.createUser(user);
            if (result == -1) {
                // User creation failed (either due to existing user or system error)
                return new ResponseEntity<>("User creation failed: User already exists or invalid data.", HttpStatus.BAD_REQUEST);
            }

            if (result == -2) {
                // User creation failed (either due to existing user or system error)
                return new ResponseEntity<>("Email is not verified", HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unexpected error creating user: ", e);
            return new ResponseEntity<>("Unexpected error creating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Login user
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwtToken = jwtUtils.generateToken(userDetails.getUsername());
            User user = userRepository.findUserByUsername(loginRequest.getUsername());
            LoginResponse loginResponse = new LoginResponse(jwtToken, user);
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Verify user token
    @GetMapping("/authenticate")
    public ResponseEntity<TokenAuthResponse> verifyToken(){
        try{
            TokenAuthResponse tokenAuthResponse = userService.verifyToken();

            // Adjust response status based on authentication result
            HttpStatus status = tokenAuthResponse.isStatus() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(tokenAuthResponse, status);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Fetch all products
    @GetMapping("/allProducts")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<FetchProductsResponse> products = productService.fetchAllProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error while fetching products", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Fetch all products
    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProductsForAdmin() {
        try {
            List<Product> all = productRepository.findAll();
            return new ResponseEntity<>(all, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching all products: " + e.getMessage());
            return new ResponseEntity<>("Unexpected error while fetching products", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Contact
    @PostMapping("/contact")
    public ResponseEntity<?> sendMail(@RequestBody EmailRequest emailRequest){
        try{
            String url = emailService.sendDraftMail(emailRequest.getSubject(), emailRequest.getBody());
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while sending email: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Fetch product by id
    @GetMapping("/getProduct/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        try {
            Product product = productService.fetchProductById(productId);

            if (product != null) {
                return new ResponseEntity<>(product, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Unexpected error fetching product: ", e);
            return new ResponseEntity<>("Unexpected error fetching product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get top sellers
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

    // Get new arrivals
    @GetMapping("/new-arrivals")
    public ResponseEntity<?> getNewArrivals(){
        try{
            List<FetchProductsResponse> fetchProductsResponses = analysisService.listAllProductsOrderedByCreation();

            List<FetchProductsResponse> newArrivals = fetchProductsResponses.stream()
                    .limit(5)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(newArrivals, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

    // Temporary method to fetch user by username
    @GetMapping("/getUserByUsername/{username}")
    public User getUserByUsername(@PathVariable String username){
        return userRepository.findUserByUsername(username);
    }

    // Temporary method to fetch user by user id
    @GetMapping("/getUserByUID/{uid}")
    public UserResponse getUserByUID(@PathVariable String uid){
        return userRepository.findUserById(uid);
    }
}
