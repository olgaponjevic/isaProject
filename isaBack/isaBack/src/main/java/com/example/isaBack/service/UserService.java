package com.example.isaBack.service;

import com.example.isaBack.dto.UserProfileResponse;
import com.example.isaBack.dto.VideoResponse;
import com.example.isaBack.model.User;
import com.example.isaBack.model.Video;
import com.example.isaBack.repository.UserRepository;
import com.example.isaBack.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public List<VideoResponse> getUserVideos(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        return videoRepository.findByAuthorIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toVideoResponse)
                .collect(Collectors.toList());
    }

    private VideoResponse toVideoResponse(Video video) {
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
                        .map(t -> t.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}