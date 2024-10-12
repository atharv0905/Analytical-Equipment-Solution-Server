package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.analyticalsolution.analyticalsolution.utils.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    // Add new product
    public int addProduct(Product product, MultipartFile productImages){
        try{
            String sql = "INSERT INTO products (product_id, product_name, product_desc, product_category, estimated_delivery_time, product_price, product_images) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            product.setProduct_id(UUID.randomUUID().toString());
            // Save product image paths
            String productImagePath = utilityService.saveProductImage(productImages, product.getProduct_name()+"_"+product.getProduct_id());

            return jdbcTemplate.update(sql,
                    product.getProduct_id(),
                    product.getProduct_name(),
                    product.getProduct_desc(),
                    product.getProduct_category(),
                    product.getEstimated_delivery_time(),
                    product.getProduct_price(),
                    productImagePath);
        }catch (Exception e){
            log.error("Unexpected error adding product: " + e.getMessage());
            return -1;
        }
    }
}
