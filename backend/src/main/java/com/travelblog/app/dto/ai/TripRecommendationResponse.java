package com.travelblog.app.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRecommendationResponse {

    private String destination;
    private String budget;
    private Integer duration;
    private String summary;
    private boolean fallback;
    private List<DayItinerary> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayItinerary {
        private int day;
        private String theme;
        private String morning;
        private String afternoon;
        private String evening;
        private String estimatedCost;
    }
}
