package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.dto.StorjNodeResponse;
import com.jvprojects.jobmaster.dto.OverviewResponse;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoSecond;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoSecondRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@RestController
@RequestMapping("/api/job")
@CrossOrigin(origins = "*")
public class ApiController {

    private final StorjNodeRepository storjNodeRepository;
    private final StorjSnoSecondRepository storjSnoSecondRepository;

    public ApiController(StorjNodeRepository storjNodeRepository, StorjSnoSecondRepository storjSnoSecondRepository) {
        this.storjNodeRepository = storjNodeRepository;
        this.storjSnoSecondRepository = storjSnoSecondRepository;
    }

    private static class BandwidthState {
        java.time.LocalDate lastDay;
        long lastValue;
        long accumulated;

        BandwidthState() {
            this.lastDay = null;
            this.lastValue = 0;
            this.accumulated = 0;
        }
    }

    @GetMapping("/nodes")
    public List<StorjNodeResponse> nodes() {
        return storjNodeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/overview")
    public OverviewResponse overview(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "5m") String interval,
                                      @org.springframework.web.bind.annotation.RequestParam(defaultValue = "30") int points) {
        Duration step = intervalDuration(interval);
        OffsetDateTime end = alignToBoundary(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS), interval, step);
        OffsetDateTime start = end.minus(step.multipliedBy(points));
        List<StorjSnoSecond> snoRecords = storjSnoSecondRepository.findAllByOrderByCreatedAtDesc();
        List<OverviewResponse.Point> resultPoints = new ArrayList<>();
        Long firstStorage = null;
        Long firstTrash = null;
        java.util.Map<String, BandwidthState> ingressState = new java.util.HashMap<>();
        java.util.Map<String, BandwidthState> egressState = new java.util.HashMap<>();

        for (int index = 0; index < points; index++) {
            OffsetDateTime bucketStart = start.plus(step.multipliedBy(index));
            OffsetDateTime bucketEnd = bucketStart.plus(step);
            List<StorjSnoSecond> bucket = snoRecords.stream()
                    .filter(record -> record.getCreatedAt() != null
                            && !record.getCreatedAt().isBefore(bucketStart)
                            && record.getCreatedAt().isBefore(bucketEnd))
                    .toList();
            Collection<StorjSnoSecond> latest = latestPerNode(bucket);
            long storage = sumOf(latest, StorjSnoSecond::getUsedDiskSpace);
            long trash = sumOf(latest, StorjSnoSecond::getTrashDiskSpace);
            long ingress = accumulateBandwidthWithDateDetection(latest, StorjSnoSecond::getIngressTotal, ingressState, bucketStart);
            long egress = accumulateBandwidthWithDateDetection(latest, StorjSnoSecond::getEgressTotal, egressState, bucketStart);
            double uptime = weightedUptimeAverage(latest);
            if (firstStorage == null && storage > 0) firstStorage = storage;
            if (firstTrash == null && trash > 0) firstTrash = trash;
            resultPoints.add(new OverviewResponse.Point(
                    bucketStart.toString(),
                    storage,
                    percentageOfFirst(storage, firstStorage),
                    trash,
                    percentageOfFirst(trash, firstTrash),
                    ingress,
                    egress,
                    uptime
            ));
        }
        return new OverviewResponse(interval, points, resultPoints);
    }

    /**
     * Aligns "now" down to the same clock boundary the collection jobs run on (top of the
     * minute/hour/day/week/month), so bucket edges land on round times like 2:00, 3:00
     * regardless of the exact moment the request was made.
     */
    private OffsetDateTime alignToBoundary(OffsetDateTime now, String interval, Duration step) {
        if ("1w".equals(interval)) {
            return now.truncatedTo(ChronoUnit.DAYS)
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        }
        if ("1mo".equals(interval)) {
            return now.truncatedTo(ChronoUnit.DAYS).with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        }
        long stepSeconds = step.getSeconds();
        long epochSeconds = now.toEpochSecond();
        long alignedEpochSeconds = epochSeconds - (epochSeconds % stepSeconds);
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(alignedEpochSeconds), now.getOffset());
    }

    private Duration intervalDuration(String interval) {
        return switch (interval) {
            case "5s" -> Duration.ofSeconds(5);
            case "15s" -> Duration.ofSeconds(15);
            case "30s" -> Duration.ofSeconds(30);
            case "5m" -> Duration.ofMinutes(5);
            case "15m" -> Duration.ofMinutes(15);
            case "30m" -> Duration.ofMinutes(30);
            case "1h" -> Duration.ofHours(1);
            case "1d" -> Duration.ofDays(1);
            case "1w" -> Duration.ofDays(7);
            case "1mo" -> Duration.ofDays(30);
            default -> Duration.ofSeconds(5);
        };
    }

    private Collection<StorjSnoSecond> latestPerNode(List<StorjSnoSecond> records) {
        return records.stream()
                .collect(java.util.stream.Collectors.toMap(StorjSnoSecond::getNodeId, record -> record,
                        (left, right) -> left.getCreatedAt().isAfter(right.getCreatedAt()) ? left : right))
                .values();
    }

    private long accumulateBandwidthWithDateDetection(Collection<StorjSnoSecond> records, Function<StorjSnoSecond, Long> valueExtractor,
                                                     java.util.Map<String, BandwidthState> statePerNode,
                                                     OffsetDateTime bucketStart) {
        long total = 0;
        java.time.LocalDate currentDay = bucketStart.toLocalDate();

        for (StorjSnoSecond record : records) {
            String nodeId = record.getNodeId();
            long currentValue = valueExtractor.apply(record) == null ? 0 : valueExtractor.apply(record);
            BandwidthState state = statePerNode.computeIfAbsent(nodeId, k -> new BandwidthState());

            // Detect day change: when date changes, add previous day's value to accumulated
            if (state.lastDay != null && !currentDay.isEqual(state.lastDay)) {
                state.accumulated += state.lastValue;
            }

            // Update state for next iteration
            state.lastDay = currentDay;
            state.lastValue = currentValue;

            total += state.accumulated + currentValue;
        }

        return total;
    }

    private long sumOf(Collection<StorjSnoSecond> records, Function<StorjSnoSecond, Long> value) {
        return records.stream().mapToLong(record -> value.apply(record) == null ? 0 : value.apply(record)).sum();
    }

    /**
     * A true average across every satellite of every node, not an average of each node's own
     * average - nodes don't all have the same number of satellites, so averaging per-node
     * averages would over-weight nodes with fewer satellites.
     */
    private double weightedUptimeAverage(Collection<StorjSnoSecond> records) {
        double sum = records.stream().mapToDouble(record -> record.getUptimeScoreSum() == null ? 0 : record.getUptimeScoreSum()).sum();
        int count = records.stream().mapToInt(record -> record.getUptimeScoreCount() == null ? 0 : record.getUptimeScoreCount()).sum();
        return count == 0 ? 100 : sum / count;
    }

    private double percentageOfFirst(long current, Long first) {
        return first == null || first == 0 ? 0 : (current * 100.0) / first;
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
                node.getColor(),
                node.getCreatedAt(),
                node.getUpdatedAt()
        );
    }

}
