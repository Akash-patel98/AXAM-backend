package com.arishi.AXAM.service;

import com.arishi.AXAM.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final String UPLOADS_DIR = "uploads/questions";

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");


    public String upload(MultipartFile file) {

        // Check file
        if (file == null || file.isEmpty()) throw new BadRequestException("File is required");

        //  Check file size
        if (file.getSize() > MAX_FILE_SIZE) throw new BadRequestException("File size must not exceed 10 MB");

        //  Check content type
        String contentType = file.getContentType();

        if (!ALLOWED_CONTENT_TYPES.contains(contentType))
            throw new BadRequestException("Only JPEG, PNG and WEBP images are allowed");

        try {

            // Create upload directory
            Path uploadPath = Paths.get(UPLOADS_DIR);

            Files.createDirectories(uploadPath);

            // Generate unique filename
            String extension = "";

            String originalFilename = file.getOriginalFilename();

            if (originalFilename != null && originalFilename.contains(".")) {

                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }


            String fileName = UUID.randomUUID() + extension;

            //Create destination path
            Path filePath = uploadPath.resolve(fileName);

            // Store file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return URL
            return "/uploads/questions/" + fileName;


        } catch (IOException e) {

            throw new RuntimeException("Failed to store file", e);
        }
    }
}