package com.example.isaBack.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private int currentPage;
    private int totalPages;
    private long totalComments;
}