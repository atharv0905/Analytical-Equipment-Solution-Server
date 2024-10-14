/**
 * File: ProductsRowMapper.java
 * Author: Atharv Mirgal
 * Description: This utility class is a custom implementation of the RowMapper interface for mapping database
 *              rows to FetchProductsResponse entities. It transforms a result set row into a FetchProductsResponse
 *              object, which is used for lightweight data retrieval. The class deserializes JSON strings
 *              representing image paths into a list and retrieves the first image for a preview. It also appends a
 *              base URL to the image path for serving the image correctly. This allows the application to fetch and
 *              present product data with a representative image efficiently.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.responses.FetchProductsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class ProductsRowMapper implements RowMapper<FetchProductsResponse> {

    private static final String BASE_URL = "http://localhost:3000/";

    @Override
    public FetchProductsResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        FetchProductsResponse product = new FetchProductsResponse();
        product.setProduct_id(rs.getString("product_id"));
        product.setProduct_name(rs.getString("product_name"));
        product.setProduct_desc(rs.getString("product_desc"));
        product.setProduct_category(rs.getString("product_category"));

        // Convert JSON string to List<String>
        String imagesJson = rs.getString("product_images");
        try {
            ArrayList<String> imagePaths = new ObjectMapper().readValue(imagesJson, new TypeReference<ArrayList<String>>() {
            });

            String firstImagePath = BASE_URL + imagePaths.get(0);

            product.setProduct_image(firstImagePath);
        } catch (Exception e) {
//            log.error("Error parsing product images JSON: " + e.getMessage());
        }

        return product;
    }
}
