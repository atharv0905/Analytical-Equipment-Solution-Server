package com.analyticalsolution.analyticalsolution.repository;

import com.analyticalsolution.analyticalsolution.entity.Order;
import com.analyticalsolution.analyticalsolution.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Product> findAll(){
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, this::mapRowToProduct);
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {

        Product product = new Product();

        product.setProduct_id(rs.getString("product_id"));
        product.setProduct_name(rs.getString("product_name"));
        product.setProduct_price(rs.getLong("product_price"));
        product.setProduct_profit(rs.getLong("product_profit"));
        return product;
    }

}
