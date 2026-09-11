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
    public OverviewResponse overview() {
        List<OverviewResponse.NodeOverviewResponse> nodeSummaries = new ArrayList<>();
        List<StorjSnoSecond> allSnoRecords = storjSnoSecondRepository.findAllByOrderByCreatedAtDesc();
        for (StorjNode node : storjNodeRepository.findAll()) {
            StorjSatellites satellites = node.getStorjSatellites();
            List<StorjSnoSecond> records = allSnoRecords.stream()
                    .filter(item -> node.getNodeId().equals(item.getNodeId()))
                    .toList();
            List<StorageDaily> storage = satellites == null || satellites.getStorageDaily() == null
                    ? List.of()
                    : satellites.getStorageDaily().stream()
                    .sorted(Comparator.comparing(StorageDaily::getIntervalStart, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            StorjSnoSecond latestSno = records.isEmpty() ? null : records.get(0);
            StorjSnoSecond firstSno = records.isEmpty() ? null : records.get(records.size() - 1);
            StorageDaily firstStorage = storage.isEmpty() ? null : storage.get(0);
            StorageDaily latestStorage = storage.isEmpty() ? null : storage.get(storage.size() - 1);
            nodeSummaries.add(new OverviewResponse.NodeOverviewResponse(
                node.getNodeId(),
                latestStorage == null ? null : latestStorage.getAtRestTotalBytes(),
                firstStorage == null ? null : firstStorage.getAtRestTotalBytes(),
                latestSno == null ? null : latestSno.getTrashDiskSpace(),
                firstSno == null ? null : firstSno.getTrashDiskSpace(),
                totalIngress(satellites),
                totalEgress(satellites),
                averageUptime(satellites)
            ));
        }
        return new OverviewResponse(nodeSummaries);
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