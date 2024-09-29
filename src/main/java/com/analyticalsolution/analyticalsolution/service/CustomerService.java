package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createCustomer(Customer customer){
        String sql = "INSERT INTO customers (customer_id, customer_name, password) VALUES (?, ?, ?)";

        return jdbcTemplate.update(sql, customer.getCustomer_id(), customer.getCustomer_name(), customer.getPassword());
    }
}
