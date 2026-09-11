package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.dto.StorjNodeResponse;
import com.jvprojects.jobmaster.dto.OverviewResponse;
import com.jvprojects.jobmaster.entities.Audits;
import com.jvprojects.jobmaster.entities.BandwidthDaily;
import com.jvprojects.jobmaster.entities.StorageDaily;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSatellites;
import com.jvprojects.jobmaster.entities.StorjSnoSecond;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoSecondRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Comparator;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@RestController
@RequestMapping("/job")
@CrossOrigin(origins = "*")
public class JobController {

    private final StorjNodeRepository storjNodeRepository;
    private final StorjSnoSecondRepository storjSnoSecondRepository;

    public JobController(StorjNodeRepository storjNodeRepository, StorjSnoSecondRepository storjSnoSecondRepository) {
        this.storjNodeRepository = storjNodeRepository;
        this.storjSnoSecondRepository = storjSnoSecondRepository;
    }

    @GetMapping("/nodes")
    public List<StorjNodeResponse> nodes() {
        return storjNodeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/overview")
    public OverviewResponse overview(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "5m") String interval) {
        Duration step = intervalDuration(interval);
        OffsetDateTime end = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        OffsetDateTime start = end.minus(step.multipliedBy(30));
        List<StorjSnoSecond> snoRecords = storjSnoSecondRepository.findAllByOrderByCreatedAtDesc();
        List<OverviewResponse.Point> points = new ArrayList<>();
        Long firstStorage = null;
        Long firstTrash = null;

        for (int index = 0; index < 30; index++) {
            OffsetDateTime bucketStart = start.plus(step.multipliedBy(index));
            OffsetDateTime bucketEnd = bucketStart.plus(step);
            List<StorjSnoSecond> bucket = snoRecords.stream()
                    .filter(record -> record.getCreatedAt() != null
                            && !record.getCreatedAt().isBefore(bucketStart)
                            && record.getCreatedAt().isBefore(bucketEnd))
                    .toList();
            long storage = latestPerNode(bucket, StorjSnoSecond::getUsedDiskSpace);
            long trash = latestPerNode(bucket, StorjSnoSecond::getTrashDiskSpace);
            if (firstStorage == null && storage > 0) firstStorage = storage;
            if (firstTrash == null && trash > 0) firstTrash = trash;
            points.add(new OverviewResponse.Point(
                    bucketStart.toString(),
                    storage,
                    percentageOfFirst(storage, firstStorage),
                    trash,
                    percentageOfFirst(trash, firstTrash),
                    bandwidthForBucket(bucketStart, bucketEnd, true),
                    bandwidthForBucket(bucketStart, bucketEnd, false),
                    uptimeForBucket(bucketStart, bucketEnd)
            ));
        }
        return new OverviewResponse(interval, 30, points);
    }

    private Duration intervalDuration(String interval) {
        return switch (interval) {
            case "15m" -> Duration.ofMinutes(15);
            case "30m" -> Duration.ofMinutes(30);
            case "1h" -> Duration.ofHours(1);
            default -> Duration.ofMinutes(5);
        };
    }

    private long latestPerNode(List<StorjSnoSecond> records, Function<StorjSnoSecond, Long> value) {
        return records.stream()
                .collect(java.util.stream.Collectors.toMap(StorjSnoSecond::getNodeId, record -> record,
                        (left, right) -> left.getCreatedAt().isAfter(right.getCreatedAt()) ? left : right))
                .values().stream().mapToLong(record -> value.apply(record) == null ? 0 : value.apply(record)).sum();
    }

    private double percentageOfFirst(long current, Long first) {
        return first == null || first == 0 ? 0 : (current * 100.0) / first;
    }

    private long bandwidthForBucket(OffsetDateTime start, OffsetDateTime end, boolean ingress) {
        return storjNodeRepository.findAll().stream()
                .map(StorjNode::getStorjSatellites)
                .filter(java.util.Objects::nonNull)
                .flatMap(satellites -> satellites.getBandwidthDaily() == null ? java.util.stream.Stream.empty() : satellites.getBandwidthDaily().stream())
                .filter(item -> item.getIntervalStart() != null && !item.getIntervalStart().isBefore(start) && item.getIntervalStart().isBefore(end))
                .mapToLong(item -> ingress
                        ? value(item.getIngressRepair()) + value(item.getIngressUsage())
                        : value(item.getEgressRepair()) + value(item.getEgressAudit()) + value(item.getEgressUsage()))
                .sum();
    }

    private double uptimeForBucket(OffsetDateTime start, OffsetDateTime end) {
        List<BigDecimal> scores = storjNodeRepository.findAll().stream()
                .map(StorjNode::getStorjSatellites).filter(java.util.Objects::nonNull)
                .flatMap(satellites -> satellites.getAudits() == null ? java.util.stream.Stream.empty() : satellites.getAudits().stream())
                .filter(audit -> audit.getCreatedAt() != null && !audit.getCreatedAt().isBefore(start) && audit.getCreatedAt().isBefore(end))
                .map(Audits::getOnlineScore).filter(java.util.Objects::nonNull).toList();
        return scores.isEmpty() ? 0 : scores.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0) * 100;
    }

    private Long totalIngress(StorjSatellites satellites) {
        if (satellites == null || satellites.getBandwidthDaily() == null) return null;
        return satellites.getBandwidthDaily().stream()
                .mapToLong(item -> value(item.getIngressRepair()) + value(item.getIngressUsage()))
                .sum();
    }

    private Long totalEgress(StorjSatellites satellites) {
        if (satellites == null || satellites.getBandwidthDaily() == null) return null;
        return satellites.getBandwidthDaily().stream()
                .mapToLong(item -> value(item.getEgressRepair()) + value(item.getEgressAudit()) + value(item.getEgressUsage()))
                .sum();
    }

    private double averageUptime(StorjSatellites satellites) {
        if (satellites == null || satellites.getAudits() == null) return 0;
        List<BigDecimal> scores = satellites.getAudits().stream()
                .map(Audits::getOnlineScore)
                .filter(java.util.Objects::nonNull)
                .toList();
        return scores.isEmpty() ? 0 : scores.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0) * 100;
    }

    private long value(Long value) {
        return value == null ? 0 : value;
    }

    private StorjNodeResponse toResponse(StorjNode node) {
            var latestSno = storjSnoSecondRepository.findFirstByNodeIdOrderByCreatedAtDesc(node.getNodeId());
            Long usedDiskSpace = latestSno == null ? null : latestSno.getUsedDiskSpace();
            Long totalDiskSpace = usedDiskSpace == null || node.getAvailableDiskSpace() == null
                ? null
                : usedDiskSpace + node.getAvailableDiskSpace();

        return new StorjNodeResponse(
                node.getId(),
                node.getNodeId(),
                node.getUrl(),
                node.getEnabled(),
                node.getAvailableDiskSpace(),
                usedDiskSpace,
                totalDiskSpace,
                node.getCreatedAt(),
                node.getUpdatedAt()
        );
    }

}