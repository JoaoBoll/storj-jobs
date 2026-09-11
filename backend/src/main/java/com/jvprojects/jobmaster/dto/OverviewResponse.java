package com.jvprojects.jobmaster.dto;

import java.util.List;

public record OverviewResponse(
    String interval,
    int points,
    List<Point> data
) {
    public record Point(
        String label,
        Long storageUsed,
        Double storagePercentOfFirst,
        Long trashUsed,
        Double trashPercentOfFirst,
        Long ingressTotal,
        Long egressTotal,
        Double uptimePercent,
        java.math.BigDecimal estimatedPayout
    ) {
    }
}