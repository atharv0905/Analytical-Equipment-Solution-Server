package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class UtilityService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.base-url}")
    private String BASE_URL;

    // Directory to save uploaded images
    @Value("${app.UPLOAD_DIR}")
    private String UPLOAD_DIR;

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

}
