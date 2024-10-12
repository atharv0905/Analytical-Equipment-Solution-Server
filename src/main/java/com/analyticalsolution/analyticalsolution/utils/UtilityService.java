package com.analyticalsolution.analyticalsolution.utils;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UtilityService {

    @Autowired
    private UserRepository userRepository;

    // Directory to save uploaded images
    private static final String UPLOAD_DIR = Paths.get("src/main/java/com/analyticalsolution/analyticalsolution/profiles/").toAbsolutePath().toString() + "/";

    // Method to save the profile image to server and return the file path
    public String saveProductImage(MultipartFile file, String productName) {
        if (file == null || file.isEmpty()) {
            return null; // No image uploaded
        }

        String fileName = productName + ".png"; // Generate the file name

        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Save the image file
            File imageFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(imageFile);
            // Return the relative path for storage in the database
            return fileName;
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            return null; // Return null if there was an error
        }
    }
}
