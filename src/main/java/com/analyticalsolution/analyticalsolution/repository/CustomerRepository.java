package com.analyticalsolution.analyticalsolution.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Bean
//    public JdbcTemplate getJdbcTemplate() {
//        return jdbcTemplate;
//    }

}
