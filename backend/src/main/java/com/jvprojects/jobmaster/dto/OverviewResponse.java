package com.jvprojects.jobmaster.dto;

import java.util.List;

public record OverviewResponse(
        long currentUsedBandwidth,
        long previousUsedBandwidth,
        long bandwidthDelta,
        List<NodeOverviewResponse> nodes
) {

    public record NodeOverviewResponse(
            String nodeId,
            Long usedBandwidth,
            Long usedDiskSpace,
            Long availableDiskSpace,
            Long totalDiskSpace
    ) {
    }
}