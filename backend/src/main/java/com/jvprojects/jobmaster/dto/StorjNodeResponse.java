package com.jvprojects.jobmaster.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StorjNodeResponse(
        UUID id,
        String nodeId,
        String url,
        Boolean enabled,
        Long availableDiskSpace,
        Long usedDiskSpace,
        Long totalDiskSpace,
        String color,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}