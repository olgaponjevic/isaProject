package com.example.isaBack.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path thumbnailsPath;
    private Path videosPath;

    @PostConstruct
    public void init() {
        try {
            thumbnailsPath = Paths.get(uploadDir, "thumbnails").toAbsolutePath();
            videosPath = Paths.get(uploadDir, "videos").toAbsolutePath();
            Files.createDirectories(thumbnailsPath);
            Files.createDirectories(videosPath);
        } catch (IOException e) {
            throw new RuntimeException("Ne mogu da kreiram upload foldere", e);
        }
    }

    public String saveThumbnail(MultipartFile file) {
        return saveFile(file, thumbnailsPath);
    }

    public String saveVideo(MultipartFile file) {
        return saveFile(file, videosPath);
    }

    public Path getThumbnailPath(String filename) {
        return thumbnailsPath.resolve(filename);
    }

    public Path getVideoPath(String filename) {
        return videosPath.resolve(filename);
    }

    public void deleteFile(String filename, boolean isThumbnail) {
        try {
            Path path = isThumbnail ? thumbnailsPath.resolve(filename) : videosPath.resolve(filename);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Ne mogu da obrišem fajl: " + filename);
        }
    }

    private String saveFile(MultipartFile file, Path targetDir) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Fajl je prazan");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID() + extension;
        Path targetPath = targetDir.resolve(uniqueFilename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Greška pri snimanju fajla", e);
        }
    }
}