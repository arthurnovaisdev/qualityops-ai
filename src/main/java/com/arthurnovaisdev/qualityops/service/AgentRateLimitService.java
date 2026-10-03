package com.arthurnovaisdev.qualityops.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentRateLimitService {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, Deque<Long>> requests =
            new ConcurrentHashMap<>();

    public boolean tryConsume() {

        String user =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        long now = Instant.now().getEpochSecond();

        Deque<Long> userRequests =
                requests.computeIfAbsent(
                        user,
                        ignored -> new ArrayDeque<>()
                );

        synchronized (userRequests) {

            while (!userRequests.isEmpty()
                    && now - userRequests.peekFirst()
                    >= WINDOW_SECONDS) {

                userRequests.pollFirst();
            }

            if (userRequests.size() >= MAX_REQUESTS) {
                return false;
            }

            userRequests.addLast(now);

            return true;
        }
    }
}