package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Add new product
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@ModelAttribute Product product, @RequestParam("productImages") MultipartFile productImages){
        try {
            if (productImages == null || productImages.isEmpty()) {
                return new ResponseEntity<>("Product images are required", HttpStatus.BAD_REQUEST);
            }

            int result = productService.addProduct(product, productImages);
            if (result == -1) {
                // Product creation failed
                return new ResponseEntity<>("Product creation failed: Invalid data.", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Unexpected error adding product: ", e);
            return new ResponseEntity<>("Unexpected error adding product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
