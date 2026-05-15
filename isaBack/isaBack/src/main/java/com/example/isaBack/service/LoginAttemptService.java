package com.example.isaBack.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 1;

    private final Map<String, List<LocalDateTime>> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        List<LocalDateTime> ipAttempts = attempts.get(ip);
        if (ipAttempts == null) {
            return false;
        }
        cleanOldAttempts(ipAttempts);
        return ipAttempts.size() >= MAX_ATTEMPTS;
    }

    public void recordFailedAttempt(String ip) {
        attempts.computeIfAbsent(ip, k -> new ArrayList<>()).add(LocalDateTime.now());
    }

    public void resetAttempts(String ip) {
        attempts.remove(ip);
    }

    private synchronized void cleanOldAttempts(List<LocalDateTime> ipAttempts) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(WINDOW_MINUTES);
        ipAttempts.removeIf(time -> time.isBefore(cutoff));
    }
}