package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.entity.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class ProductRowMapper implements RowMapper<Product> {

    private static final String BASE_URL = "http://localhost:3000/";

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProduct_id(rs.getString("product_id"));
        product.setProduct_name(rs.getString("product_name"));
        product.setProduct_desc(rs.getString("product_desc"));
        product.setProduct_category(rs.getString("product_category"));
        product.setEstimated_delivery_time(rs.getString("estimated_delivery_time"));
        product.setProduct_price(rs.getLong("product_price"));

        // Convert JSON string to List<String>
        String imagesJson = rs.getString("product_images");
        try {
            ArrayList<String> imagePaths = new ObjectMapper().readValue(imagesJson, new TypeReference<ArrayList<String>>() {
            });

            ArrayList<String> fullImagePaths = new ArrayList<>();

            for (String imagePath : imagePaths) {
                fullImagePaths.add(BASE_URL + imagePath);
            }

            product.setProduct_images(fullImagePaths);
        } catch (Exception e) {
//            log.error("Error parsing product images JSON: " + e.getMessage());
        }

        return product;
    }
}
