package com.example.sessiondemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class TabCountController {
    private final Map<String, Integer> userTabCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> userTabLastUpdated = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> logoutFutures = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long LOGOUT_DELAY = 2; // Delay in seconds before logout

    @PostMapping("/updateTabCount")
    public ResponseEntity<Void> updateTabCount(@RequestBody Map<String, Integer> request,
                                               HttpSession session) {
        Integer count = request.get("count");
        String sessionId = session.getId();

        // Update the tab count and last updated time for the session
        userTabCounts.put(sessionId, count);
        userTabLastUpdated.put(sessionId, System.currentTimeMillis());

        // Cancel any existing logout future
        ScheduledFuture<?> existingFuture = logoutFutures.remove(sessionId);
        if (existingFuture != null) {
            existingFuture.cancel(false);
        }

        if (count == 0) {
            // Schedule a task to check if the session should be invalidated
            ScheduledFuture<?> future = scheduler.schedule(() -> {
                if (userTabCounts.getOrDefault(sessionId, 0) == 0) {
                    invalidateSession(sessionId, session);
                }
            }, LOGOUT_DELAY, TimeUnit.SECONDS);

            // Store the future for cancellation if needed
            logoutFutures.put(sessionId, future);
        }

        return ResponseEntity.ok().build();
    }

    private void invalidateSession(String sessionId, HttpSession session) {
        userTabCounts.remove(sessionId);
        userTabLastUpdated.remove(sessionId);
        session.invalidate();
        // Example: Invalidate the session from the session management service
        // sessionManagementService.invalidateSession(sessionId);

    }

}
