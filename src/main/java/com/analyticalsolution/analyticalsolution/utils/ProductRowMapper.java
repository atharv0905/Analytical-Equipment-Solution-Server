package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.entity.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProduct_id(rs.getString("product_id"));
        product.setProduct_name(rs.getString("product_name"));
        product.setProduct_desc(rs.getString("product_desc"));
        product.setProduct_category(rs.getString("product_category"));
        product.setEstimated_delivery_time(rs.getString("estimated_delivery_time"));
        product.setProduct_price(rs.getLong("product_price"));
        return product;
    }
}
