package com.jvprojects.jobmaster.dto;

import java.util.List;

public record OverviewResponse(List<NodeOverviewResponse> nodes) {

    public record NodeOverviewResponse(
            String nodeId,
            Long storageUsed,
            Long storageFirstInterval,
            Long trashUsed,
            Long trashFirstInterval,
            Long ingressTotal,
            Long egressTotal,
            Double uptimePercent
    ) {
    }
}