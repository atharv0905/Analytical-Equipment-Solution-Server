/**
 * File: PublicController.java
 * Author: Atharv Mirgal
 * Description: This controller manages public API endpoints for user authentication, registration, and product retrieval.
 *              It includes methods for user signup, login, token verification, and server status checks, offering secure
 *              authentication using JWT. Additionally, it provides endpoints for fetching all products or a product by ID,
 *              and includes temporary methods for fetching user details by username or user ID for testing purposes.
 *              Utilizes Spring Security for authentication and logs errors to assist in troubleshooting.
 * Created on: 11/10/2024
 * Last Modified: 13/10/2024
 */


package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.requests.LoginRequest;
import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.FetchProductsResponse;
import com.analyticalsolution.analyticalsolution.responses.LoginResponse;
import com.analyticalsolution.analyticalsolution.responses.TokenAuthResponse;
import com.analyticalsolution.analyticalsolution.service.ProductService;
import com.analyticalsolution.analyticalsolution.service.UserDetailsServiceImpl;
import com.analyticalsolution.analyticalsolution.service.UserService;
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
    private JwtUtils jwtUtils;

    @Autowired
    private ProductService productService;

    // Check whether the server is running
    @GetMapping()
    public String serverCheck(){
        return "Server is running...";
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
            return new ResponseEntity<>(loginResponsestat, HttpStatus.OK);
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

    // Temporary method to fetch user by username
    @GetMapping("/getUserByUsername/{username}")
    public User getUserByUsername(@PathVariable String username){
        return userRepository.findUserByUsername(username);
    }

    // Temporary method to fetch user by user id
    @GetMapping("/getUserByUID/{uid}")
    public User getUserByUID(@PathVariable String uid){
        return userRepository.findUserById(uid);
    }
}
