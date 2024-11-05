/**
 * File: ProductController.java
 * Author: Atharv Mirgal
 * Description: This controller handles API endpoints related to product management, including adding, updating,
 *              and deleting products. It accepts product details and multiple images for product creation and updates,
 *              ensuring all required fields are provided. It uses the ProductService for business logic and handles
 *              potential errors gracefully with appropriate responses. All operations log relevant messages to assist
 *              with troubleshooting and maintain transparency in product-related actions.
 * Created on: 12/10/2024
 * Last Modified: 27/10/2024
 */

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
    public ResponseEntity<?> addProduct(@ModelAttribute Product product, @RequestParam("productImages") MultipartFile[] productImages){
        try {
            if (productImages == null || productImages.length == 0) {
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

    // Delete product
    /*
       This delete functionality is moved to update status of product
    @DeleteMapping("/delete")
    */
    public ResponseEntity<?> deleteProduct(@RequestParam("productID") String productID) {
        try {
            // Call the service method to delete the product
            int result = productService.deleteProduct(productID);

            if (result > 0) {
                return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Unexpected error deleting product: ", e);
            return new ResponseEntity<>("Unexpected error deleting product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update product
    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@ModelAttribute Product product, @RequestParam(value = "productImages", required = false) MultipartFile[] productImages) {
        try {

            if (productImages == null || productImages.length == 0) {
                int result = productService.updateProductWithoutImages(product);
                if (result == -1) {
                    return new ResponseEntity<>("Product update failed: Invalid data.", HttpStatus.BAD_REQUEST);
                }
            }else {
                int result = productService.updateProduct(product, productImages);
                if (result == -1) {
                    return new ResponseEntity<>("Product update failed: Invalid data.", HttpStatus.BAD_REQUEST);
                }
            }

            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unexpected error updating product: ", e);
            return new ResponseEntity<>("Unexpected error updating product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
