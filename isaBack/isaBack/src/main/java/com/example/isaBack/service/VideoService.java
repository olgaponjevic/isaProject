package com.example.isaBack.service;

import com.example.isaBack.dto.CreateVideoRequest;
import com.example.isaBack.dto.VideoResponse;
import com.example.isaBack.model.Tag;
import com.example.isaBack.model.User;
import com.example.isaBack.model.Video;
import com.example.isaBack.repository.TagRepository;
import com.example.isaBack.repository.UserRepository;
import com.example.isaBack.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.upload.simulate-slow}")
    private boolean simulateSlow;

    @Value("${app.upload.timeout-seconds}")
    private int timeoutSeconds;

    public List<VideoResponse> getAllVideos() {
        return videoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VideoResponse getVideoById(Long id) {
        videoRepository.incrementViews(id);
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video nije pronađen"));
        return toResponse(video);
    }

    public Video getRawVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video nije pronađen"));
    }

    @Transactional(timeout = 30)
    public VideoResponse createVideo(CreateVideoRequest request, MultipartFile thumbnailFile, MultipartFile videoFile, String authorEmail) {
        String thumbnailFilename = null;
        String videoFilename = null;

        try {
            User author = userRepository.findByEmail(authorEmail)
                    .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

            thumbnailFilename = fileStorageService.saveThumbnail(thumbnailFile);
            videoFilename = fileStorageService.saveVideo(videoFile);

            if (simulateSlow) {
                try {
                    Thread.sleep(35000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            Set<Tag> videoTags = new HashSet<>();
            if (request.getTags() != null) {
                for (String tagName : request.getTags()) {
                    Tag tag = tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                    videoTags.add(tag);
                }
            }

            Video video = Video.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .thumbnailPath(thumbnailFilename)
                    .videoPath(videoFilename)
                    .createdAt(LocalDateTime.now())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .author(author)
                    .tags(videoTags)
                    .build();

            Video saved = videoRepository.save(video);
            return toResponse(saved);

        } catch (Exception e) {
            if (thumbnailFilename != null) {
                fileStorageService.deleteFile(thumbnailFilename, true);
            }
            if (videoFilename != null) {
                fileStorageService.deleteFile(videoFilename, false);
            }
            throw new RuntimeException("Greška pri kreiranju videa: " + e.getMessage(), e);
        }
    }

    private VideoResponse toResponse(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .thumbnailUrl("/api/files/thumbnail/" + video.getId())
                .videoUrl("/api/files/video/" + video.getId())
                .createdAt(video.getCreatedAt())
                .latitude(video.getLatitude())
                .longitude(video.getLongitude())
                .authorUsername(video.getAuthor().getUsername())
                .authorId(video.getAuthor().getId())
                .views(video.getViews())
                .tags(video.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}