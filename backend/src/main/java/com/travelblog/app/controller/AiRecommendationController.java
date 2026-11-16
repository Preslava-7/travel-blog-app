package com.travelblog.app.controller;

import com.travelblog.app.dto.ai.TripRecommendationRequest;
import com.travelblog.app.dto.ai.TripRecommendationResponse;
import com.travelblog.app.service.AiRecommendationService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/recommendation")
    public ResponseEntity<TripRecommendationResponse> getRecommendation(
            @Valid @RequestBody TripRecommendationRequest request) {
        TripRecommendationResponse response = aiRecommendationService.generateItinerary(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Object> handleRateLimitExceeded(RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                        "error", "Too Many Requests",
                        "message", "AI recommendation rate limit exceeded. Please try again in a minute."
                ));
    }
}