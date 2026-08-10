//package com.arishi.AXAM.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@RequiredArgsConstructor
//public class FileStorageService {
//    public static String upload(MultipartFile image) {
//
//        // leter we impllemnt this class
//
//        return null;
//    }
//}

package com.arishi.AXAM.service;

import com.arishi.AXAM.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private static final long max_file_size = 10 * 1024 * 1024;
    private static final String uploads_dir = "uploads/questions";

    public String upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getSize() > max_file_size) {
            throw new BadRequestException("File size must not exceed 10 MB");
        }

        try {
            Path uploadPath = Paths.get(uploads_dir);
            Files.createDirectories(uploadPath);

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/questions/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}