package com.travelblog.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelblog.app.dto.ai.TripRecommendationRequest;
import com.travelblog.app.dto.ai.TripRecommendationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.model}")
    private String model;

    @RateLimiter(name = "aiRecommendationLimiter")
    @CircuitBreaker(name = "aiRecommendationBreaker", fallbackMethod = "generateFallbackItinerary")
    public TripRecommendationResponse generateItinerary(TripRecommendationRequest request) {
        String prompt = buildPrompt(request);

        Map<String, Object> body = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a travel planner API. Respond ONLY with valid JSON."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        String rawResponse = aiRestClient.post()
                .body(body)
                .retrieve()
                .body(String.class);

        return parseAiResponse(rawResponse, request);
    }

    private String buildPrompt(TripRecommendationRequest request) {
        String prefs = (request.getPreferences() == null || request.getPreferences().isEmpty())
                ? "general sightseeing and local food"
                : String.join(", ", request.getPreferences());

        return """
                Create a %d-day travel itinerary for %s with a total budget of %s.
                Traveler preferences: %s.
                Return strictly a JSON object with this exact structure:
                {
                  "destination": "%s",
                  "budget": "%s",
                  "duration": %d,
                  "summary": "Short overview of the trip",
                  "days": [
                    {
                      "day": 1,
                      "theme": "Day theme",
                      "morning": "Morning activity",
                      "afternoon": "Afternoon activity",
                      "evening": "Evening activity",
                      "estimatedCost": "Estimated daily cost"
                    }
                  ]
                }
                """.formatted(
                request.getDuration(),
                request.getDestination(),
                request.getBudget(),
                prefs,
                request.getDestination(),
                request.getBudget(),
                request.getDuration()
        );
    }

    private TripRecommendationResponse parseAiResponse(String rawResponse, TripRecommendationRequest request) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText();
            String cleanJson = content.replaceAll("^```json\\s*|\\s*```$", "").trim();

            TripRecommendationResponse parsed = objectMapper.readValue(cleanJson, TripRecommendationResponse.class);
            parsed.setFallback(false);
            return parsed;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI response", e);
        }
    }

    public TripRecommendationResponse generateFallbackItinerary(TripRecommendationRequest request, Throwable ex) {
        log.warn("AI service unavailable for destination '{}'. Serving fallback itinerary. Reason: {}",
                request.getDestination(), ex.getMessage());

        List<TripRecommendationResponse.DayItinerary> fallbackDays = new ArrayList<>();
        for (int day = 1; day <= request.getDuration(); day++) {
            fallbackDays.add(TripRecommendationResponse.DayItinerary.builder()
                    .day(day)
                    .theme("Explore " + request.getDestination() + " - Day " + day)
                    .morning("Walking tour of the historic center and main landmarks")
                    .afternoon("Visit top-rated local museums and cultural spots")
                    .evening("Traditional dinner in a local neighborhood")
                    .estimatedCost("Fits within " + request.getBudget() + " total budget")
                    .build());
        }

        return TripRecommendationResponse.builder()
                .destination(request.getDestination())
                .budget(request.getBudget())
                .duration(request.getDuration())
                .summary("Standard itinerary generated while AI service is temporarily unavailable.")
                .fallback(true)
                .days(fallbackDays)
                .build();
    }
}