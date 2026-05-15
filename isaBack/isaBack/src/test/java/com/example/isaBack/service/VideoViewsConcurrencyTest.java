package com.example.isaBack.service;

import com.example.isaBack.model.Role;
import com.example.isaBack.model.User;
import com.example.isaBack.model.Video;
import com.example.isaBack.repository.UserRepository;
import com.example.isaBack.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class VideoViewsConcurrencyTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldIncrementViewsCorrectlyWithConcurrentRequests() throws InterruptedException {
        User testUser = userRepository.save(User.builder()
                .email("test-views@example.com")
                .username("test-views-user")
                .password("hashed")
                .firstName("Test")
                .lastName("User")
                .address("Test")
                .enabled(true)
                .role(Role.USER)
                .build());

        Video testVideo = videoRepository.save(Video.builder()
                .title("Concurrency test video")
                .description("Test")
                .thumbnailPath("test.jpg")
                .videoPath("test.mp4")
                .createdAt(LocalDateTime.now())
                .author(testUser)
                .views(0L)
                .build());

        Long videoId = testVideo.getId();
        int numberOfThreads = 100;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    videoService.getVideoById(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        Video result = videoRepository.findById(videoId).orElseThrow();
        System.out.println(">>> FINALNI BROJ PREGLEDA: " + result.getViews() + " (očekivano: " + numberOfThreads + ")");
        assertEquals((long) numberOfThreads, result.getViews());

        videoRepository.delete(result);
        userRepository.delete(testUser);
    }
}