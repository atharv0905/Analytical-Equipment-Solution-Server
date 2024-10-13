package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.utils.ProductRowMapper;
import com.analyticalsolution.analyticalsolution.utils.UtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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
    public int addProduct(Product product, MultipartFile[] productImages) {
        try {
            String sql = "INSERT INTO products (product_id, product_name, product_desc, product_category, estimated_delivery_time, product_price, product_images) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

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

            // Update the database with the product information
            return jdbcTemplate.update(sql,
                    product.getProduct_id(),
                    product.getProduct_name(),
                    product.getProduct_desc(),
                    product.getProduct_category(),
                    product.getEstimated_delivery_time(),
                    product.getProduct_price(),
                    imagesJson);
        } catch (Exception e) {
            log.error("Unexpected error adding product: " + e.getMessage());
            return -1;
        }
    }

    // Fetch all products
    public List<Product> fetchAllProducts() {
        try {
            String sql = "SELECT product_id, product_name, product_desc, product_category, estimated_delivery_time, product_price, product_images FROM products";

            return jdbcTemplate.query(sql, new ProductRowMapper());
        } catch (Exception e) {
            log.error("Unexpected error fetching products: " + e.getMessage());
            return null;
        }
    }

}
