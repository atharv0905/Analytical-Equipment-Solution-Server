/**
 * File: UtilityService.java
 * Author: Atharv Mirgal
 * Description: This service provides utility functions for handling file operations, including saving and deleting
 *              product images on the server. It supports storing image files in a specified upload directory
 *              and returning relative paths for database storage. Additionally, it allows for deleting images
 *              associated with products when they are updated or removed.
 *              The service uses the Spring @Value annotation to load the base URL and upload directory from
 *              application properties, ensuring flexibility in file storage configuration.
 * Created on: 13/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UtilityService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.base-url}")
    private String BASE_URL = "http://localhost:3000/";

    // Directory to save uploaded images
//    @Value("${app.UPLOAD_DIR}")
    private String UPLOAD_DIR = "D:/AES/ProductImages/";

    // Method to save the profile image to server and return the file path
    public String saveProductImage(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            System.out.println("File is empty");
            return null; // No image uploaded
        }

        String fileName = username + ".png"; // Generate the file name
        fileName = fileName.replaceAll(" ", "");
        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Use Paths.get for better file handling
            File imageFile = Paths.get(UPLOAD_DIR, fileName).toFile();

            // Save the image file
            file.transferTo(imageFile);

            // Return the relative path for storage in the database
            return fileName;
        } catch (IOException e) {
            return null; // Return null if there was an error
        }
    }

    // Method to delete images
    public void deleteImages(List<String> imagePaths) {
        for (String imagePath : imagePaths) {
            try {
                File file = new File(UPLOAD_DIR, imagePath);
                if (file.exists() && file.delete()) {
                    log.info("Deleted image: " + imagePath);
                } else {
                    log.warn("Failed to delete image: " + imagePath);
                }
            } catch (Exception e) {
                log.error("Error deleting image file: " + e.getMessage());
            }
        }
    }

    // Helper method to parse a JSON string into a List of strings
    public List<String> parseJsonToList(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Failed to parse JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Helper method to convert a List of strings to a JSON string
    public String convertListToJson(List<String> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.error("Failed to convert list to JSON: " + e.getMessage());
            return "[]";
        }
    }
}
