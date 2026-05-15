package com.example.isaBack.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VideoResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private LocalDateTime createdAt;
    private Double latitude;
    private Double longitude;
    private String authorUsername;
    private Long authorId;
    private Long views;
    private Set<String> tags;
}