package ir.maktab.shortcut.fianlproject.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    @Value("${upload.dir}")
    private String uploadDir;
    public String storeFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());


        Path targetLocation = uploadPath.resolve(fileName);


        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
