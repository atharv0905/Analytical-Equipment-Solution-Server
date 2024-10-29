/**
 * File: ProductService.java
 * Author: Atharv Mirgal
 * Description: This service class provides methods for managing product-related operations, including adding,
 *              fetching, updating, and deleting products. It interacts with the database using JdbcTemplate
 *              and handles operations related to image storage for products. This class also uses a utility
 *              service for handling file operations and ensures data integrity while managing product details.
 *              The service is designed to handle various exceptions gracefully, logging errors and providing
 *              meaningful responses.
 * Created on: 12/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.responses.FetchProductsResponse;
import com.analyticalsolution.analyticalsolution.utils.ProductRowMapper;
import com.analyticalsolution.analyticalsolution.utils.ProductsRowMapper;
import com.analyticalsolution.analyticalsolution.utils.UtilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    // Add new product
    @Transactional
    public int addProduct(Product product, MultipartFile[] productImages) {
        try {
            String sql = "INSERT INTO products (product_id, product_name, product_desc, product_category, estimated_delivery_time, product_price, product_profit, product_status, product_images) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Generate a unique ID for the product
            product.setProduct_id(UUID.randomUUID().toString());
            // Save product images paths
            ArrayList<String> imagePaths = new ArrayList<>();

            for (MultipartFile image : productImages) {
                // Save the image and get the relative path
                String imagePath = utilityService.saveProductImage(image, product.getProduct_name() + "_" + UUID.randomUUID());
                if (imagePath != null) {
                    // Prepend base URL to the image path
                    String fullPath = imagePath; // Adjust as needed based on your file storage structure
                    imagePaths.add(fullPath);
                }
            }

            // Convert image paths list to JSON string for database storage
            String imagesJson = new ObjectMapper().writeValueAsString(imagePaths);
            Boolean defaultProductStatus = true;
            // Update the database with the product information
            return jdbcTemplate.update(sql,
                    product.getProduct_id(),
                    product.getProduct_name(),
                    product.getProduct_desc(),
                    product.getProduct_category(),
                    product.getEstimated_delivery_time(),
                    product.getProduct_price(),
                    product.getProduct_profit(),
                    defaultProductStatus,
                    imagesJson);
        } catch (Exception e) {
            log.error("Unexpected error adding product: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }

    // Fetch all products
    @Transactional
    public List<FetchProductsResponse> fetchAllProducts() {
        try {
            String sql = "SELECT product_id, product_name, product_desc, product_category, product_images FROM products";

            return jdbcTemplate.query(sql, new ProductsRowMapper());
        } catch (Exception e) {
            log.error("Unexpected error fetching products: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    // Fetch product by ID
    @Transactional
    public Product fetchProductById(String productId) {
        try {
            // Select all columns from the products table where product_id matches
            String sql = "SELECT product_id, product_name, product_desc, product_category, estimated_delivery_time, product_price, product_profit, product_images FROM products WHERE product_id = ?";

            // Use queryForObject to get a single product's details
            return jdbcTemplate.queryForObject(sql, new Object[]{productId}, new ProductRowMapper());
        } catch (Exception e) {
            log.error("Unexpected error fetching product with ID " + productId + ": " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    // Delete product
    @Transactional
    public int deleteProduct(String productId) {
        try {
            // Fetch the product to get image paths
            String sqlSelect = "SELECT product_images FROM products WHERE product_id = ?";
            String imagesJson = jdbcTemplate.queryForObject(sqlSelect, new Object[]{productId}, String.class);
            System.out.println(imagesJson);
            if (imagesJson != null) {
                // Convert JSON string back to List
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> imagePaths = objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});

                // Delete the images from the file system
                utilityService.deleteImages(imagePaths);
            }

            // Delete the product from the database
            String sqlDelete = "DELETE FROM products WHERE product_id = ?";
            return jdbcTemplate.update(sqlDelete, productId);
        } catch (Exception e) {
            log.error("Unexpected error deleting product: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }

    // Update product
    @Transactional
    public int updateProduct(Product product, MultipartFile[] productImages) {
        try {
            // Fetch the existing product to get the old image paths
            String sqlSelect = "SELECT product_images FROM products WHERE product_id = ?";
            String imagesJson = jdbcTemplate.queryForObject(sqlSelect, new Object[]{product.getProduct_id()}, String.class);

            if (imagesJson != null) {
                // Convert JSON string back to List
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> oldImagePaths = objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});

                // Delete the old images
                utilityService.deleteImages(oldImagePaths);
            }

            // Save new product images paths
            ArrayList<String> imagePaths = new ArrayList<>();
            for (MultipartFile image : productImages) {
                // Save the image and get the relative path
                String imagePath = utilityService.saveProductImage(image, product.getProduct_name() + "_" + UUID.randomUUID());
                if (imagePath != null) {
                    imagePaths.add(imagePath);
                }
            }

            // Convert image paths list to JSON string for database storage
            String imagesJsonNew = new ObjectMapper().writeValueAsString(imagePaths);

            // Update the database with the new product information
            String sqlUpdate = "UPDATE products SET product_name = ?, product_desc = ?, product_category = ?, estimated_delivery_time = ?, product_price = ?, product_profit = ?, product_status = ?, product_images = ? WHERE product_id = ?";
            return jdbcTemplate.update(sqlUpdate,
                    product.getProduct_name(),
                    product.getProduct_desc(),
                    product.getProduct_category(),
                    product.getEstimated_delivery_time(),
                    product.getProduct_price(),
                    product.getProduct_profit(),
                    product.getProduct_status(),
                    imagesJsonNew,
                    product.getProduct_id());
        } catch (Exception e) {
            log.error("Unexpected error updating product: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }

}
