package com.example.isaBack.service;

import com.example.isaBack.model.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ThumbnailCacheService {

    private final VideoService videoService;
    private final FileStorageService fileStorageService;

    @Cacheable(value = "thumbnails", key = "#videoId")
    public byte[] getThumbnailBytes(Long videoId) {
        System.out.println(">>> Učitavanje thumbnail-a sa DISKA za video id=" + videoId);
        Video video = videoService.getRawVideo(videoId);
        Path path = fileStorageService.getThumbnailPath(video.getThumbnailPath());
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Ne mogu da pročitam thumbnail", e);
        }
    }

    public String getThumbnailContentType(Long videoId) {
        Video video = videoService.getRawVideo(videoId);
        Path path = fileStorageService.getThumbnailPath(video.getThumbnailPath());
        try {
            String contentType = Files.probeContentType(path);
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    @CacheEvict(value = "thumbnails", key = "#videoId")
    public void evictThumbnail(Long videoId) {
    }
}
