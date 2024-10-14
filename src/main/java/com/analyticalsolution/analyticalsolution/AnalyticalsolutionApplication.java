/**
 * File: AnalyticalsolutionApplication.java
 * Author: Atharv Mirgal
 * Description: The main entry point for the Analytical Solution Spring Boot application.
 *              It enables transaction management, configures CORS settings for cross-origin requests,
 *              and provides beans for RestTemplate and WebMvcConfigurer to support HTTP requests
 *              and CORS policies.
 * Created on: 11/10/2024
 * Last Modified: 11/10/2024
 */


package com.analyticalsolution.analyticalsolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableTransactionManagement
public class AnalyticalsolutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticalsolutionApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*") // Replace with your frontend URL if different
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(false);
			}
		};
	}
}
