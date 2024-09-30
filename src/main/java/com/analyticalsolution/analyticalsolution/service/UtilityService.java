package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Directory to save uploaded images
    private static final String UPLOAD_DIR = Paths.get("src/main/java/com/analyticalsolution/analyticalsolution/profiles/").toAbsolutePath().toString() + "/";

    // Method to save the profile image to server and return the file path
    public String saveProfileImage(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            System.out.println("File is empty");
            return null; // No image uploaded
        }

        String fileName = username + ".png"; // Generate the file name

        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Save the image file
            File imageFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(imageFile);
            System.out.println("File transferred successfully");

            // Return the relative path for storage in the database
            return "profiles/" + fileName;
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            return null; // Return null if there was an error
        }
    }

    // Method to fetch the profile image file based on username
    public File fetchProfileImage(String username) {
        // Find user by username
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            return null; // Return null if user not found
        }

        String profilePath = user.getProfile_path();
        System.out.println("Profile path: "+ UPLOAD_DIR+profilePath);
        File profileImageFile = new File(UPLOAD_DIR + profilePath);

        if (!profileImageFile.exists()) {
            System.out.println("Profile image does not exist");
            return null; // Return null if the file does not exist
        }

        return profileImageFile; // Return the profile image file
    }

}
