package com.example.isaBack.service;

import com.example.isaBack.dto.CommentRequest;
import com.example.isaBack.dto.CommentResponse;
import com.example.isaBack.dto.CommentPageResponse;
import com.example.isaBack.model.Comment;
import com.example.isaBack.model.User;
import com.example.isaBack.model.Video;
import com.example.isaBack.repository.CommentRepository;
import com.example.isaBack.repository.UserRepository;
import com.example.isaBack.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int COMMENTS_PER_PAGE = 5;
    private static final int MAX_COMMENTS_PER_HOUR = 60;

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "comments", key = "#videoId + ':' + #page")
    public CommentPageResponse getCommentsForVideo(Long videoId, int page) {
        System.out.println(">>> Učitavanje komentara IZ BAZE za video id=" + videoId + ", stranica=" + page);

        Pageable pageable = PageRequest.of(page, COMMENTS_PER_PAGE);
        Page<Comment> commentPage = commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId, pageable);

        List<CommentResponse> comments = commentPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return CommentPageResponse.builder()
                .comments(comments)
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .totalComments(commentPage.getTotalElements())
                .build();
    }

    @Transactional
    @CacheEvict(value = "comments", allEntries = true)
    public CommentResponse createComment(Long videoId, String content, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentCount = commentRepository.countByAuthorIdAndCreatedAtAfter(author.getId(), oneHourAgo);

        if (recentCount >= MAX_COMMENTS_PER_HOUR) {
            throw new RateLimitExceededException("Premašen je limit od " + MAX_COMMENTS_PER_HOUR + " komentara po satu. Pokušajte kasnije.");
        }

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video nije pronađen"));

        Comment comment = Comment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .video(video)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorUsername(comment.getAuthor().getUsername())
                .authorId(comment.getAuthor().getId())
                .build();
    }
}