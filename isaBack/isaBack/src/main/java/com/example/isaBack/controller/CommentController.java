package com.example.isaBack.controller;

import com.example.isaBack.dto.CommentPageResponse;
import com.example.isaBack.dto.CommentRequest;
import com.example.isaBack.dto.CommentResponse;
import com.example.isaBack.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentPageResponse> getComments(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(commentService.getCommentsForVideo(videoId, page));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long videoId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        String authorEmail = authentication.getName();
        CommentResponse response = commentService.createComment(videoId, request.getContent(), authorEmail);
        return ResponseEntity.ok(response);
    }
}