package com.example.isaBack.controller;

import com.example.isaBack.dto.UserProfileResponse;
import com.example.isaBack.dto.VideoResponse;
import com.example.isaBack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @GetMapping("/{id}/videos")
    public ResponseEntity<List<VideoResponse>> getUserVideos(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserVideos(id));
    }
}