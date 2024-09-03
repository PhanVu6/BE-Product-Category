package com.example.managerproduct.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ImageProductController {

    private final String IMAGE_DIRECTORY = "D:/MyProject/ImageProduct/";

    @GetMapping("/api/images")
    public ResponseEntity<Resource> getImage(@RequestParam("imageName") String imageName) {
        try {
            File file = new File(IMAGE_DIRECTORY + imageName);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Mặc định nếu không thể xác định loại nội dung
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (IOException e) {
            // Ghi log lỗi và trả về lỗi 500 nếu có sự cố đọc tệp
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
