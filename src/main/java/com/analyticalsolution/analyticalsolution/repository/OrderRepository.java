/**
 * File: OrderRepository.java
 * Author: Atharv Mirgal
 * Description: This repository class provides methods for database operations related to orders, including
 *              fetching all orders and retrieving orders within a specific date range. Using JdbcTemplate, it
 *              directly interacts with the database, mapping result sets to Order entity objects for further
 *              use within the application. The class utilizes a private helper method to map each row in the
 *              result set to an Order object. The repository handles database connectivity, ensuring efficient
 *              data access and streamlined query handling.
 * Created on: 28/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.repository;

import com.analyticalsolution.analyticalsolution.entity.Order;
import com.analyticalsolution.analyticalsolution.entity.Sale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Order> findAll(String tableName){
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.query(sql, this::mapRowToOrder);
    }

    public List<Order> findOrdersByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, this::mapRowToOrder, startDate, endDate);
    }

    private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
        return new Order(
                rs.getString("order_id"),
                rs.getString("sale_id"),
                rs.getString("product_id"),
                rs.getLong("quantity"),
                rs.getDate("order_date"),
                rs.getLong("product_price"),
                rs.getLong("product_profit")
        );
    }

    public Sale findSaleById(String sale_id){
        try {
            String sql = "SELECT * FROM sales WHERE sale_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{sale_id}, (rs, rowNum) -> {
                return new Sale(
                        rs.getString("sale_id"),
                        rs.getString("customer_id"),
                        rs.getString("order_confirmation_status"),
                        rs.getString("order_status"),
                        rs.getString("shipping_address"),
                        rs.getLong("contact_phone"),
                        rs.getString("transaction_id"),
                        rs.getString("payment_status"),
                        rs.getString("order_id"),
                        rs.getString("sale_mode")
                );
            });
        } catch (Exception e) {
            log.error("Error fetching sale record: " + e.getMessage());
            return null;
        }
    }

    private Sale mapRowToSale(ResultSet rs, int rowNum) throws SQLException {
        return new Sale(
                rs.getString("sale_id"),
                rs.getString("customer_id"),
                rs.getString("order_confirmation_status"),
                rs.getString("order_status"),
                rs.getString("shipping_address"),
                rs.getLong("contact_phone"),
                rs.getString("transaction_id"),
                rs.getString("payment_status"),
                rs.getString("order_id"),
                rs.getString("sale_mode")
        );
    }

}
