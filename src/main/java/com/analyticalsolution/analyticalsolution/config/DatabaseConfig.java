/**
 * File: DatabaseConfig.java
 * Author: Atharv Mirgal
 * Description: This configuration file sets up the MySQL database connection for the application.
 *              It provides a DataSource bean for managing database connections and a JdbcTemplate
 *              bean for executing SQL queries.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */

package com.analyticalsolution.analyticalsolution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // MySQL database connection
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/analytical_equipment_solutions"); // url
        dataSource.setUsername("Atharv"); // username
        dataSource.setPassword("atharv09"); // password
        return dataSource;
    }

    // Returns a new jdbc template
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
