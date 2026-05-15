package com.example.isaBack.controller;

import com.example.isaBack.dto.CreateVideoRequest;
import com.example.isaBack.dto.VideoResponse;
import com.example.isaBack.model.Video;
import com.example.isaBack.service.FileStorageService;
import com.example.isaBack.service.ThumbnailCacheService;
import com.example.isaBack.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final FileStorageService fileStorageService;
    private final ThumbnailCacheService thumbnailCacheService;

    @GetMapping("/videos")
    public ResponseEntity<List<VideoResponse>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @PostMapping(value = "/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> createVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "tags", required = false) Set<String> tags,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("video") MultipartFile video,
            Authentication authentication
    ) {
        CreateVideoRequest request = new CreateVideoRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setTags(tags != null ? tags : new HashSet<>());
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        String authorEmail = authentication.getName();
        VideoResponse response = videoService.createVideo(request, thumbnail, video, authorEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/thumbnail/{videoId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long videoId) {
        byte[] bytes = thumbnailCacheService.getThumbnailBytes(videoId);
        String contentType = thumbnailCacheService.getThumbnailContentType(videoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(bytes);
    }

    @GetMapping("/files/video/{videoId}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long videoId) throws Exception {
        Video raw = videoService.getRawVideo(videoId);
        Path path = fileStorageService.getVideoPath(raw.getVideoPath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + raw.getVideoPath() + "\"")
                .body(resource);
    }
}