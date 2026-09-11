package com.jvprojects.jobmaster.dto;

public record OverviewResponse(
        long currentUsedBandwidth,
        long previousUsedBandwidth,
        long bandwidthDelta
) {
}