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
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        Map<String, List<StorjSnoSecond>> byNode = storjSnoSecondRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(item -> item.getNodeId() != null)
                .collect(Collectors.groupingBy(StorjSnoSecond::getNodeId));

        long current = 0;
        long previous = 0;
        List<OverviewResponse.NodeOverviewResponse> nodeSummaries = new ArrayList<>();
        for (List<StorjSnoSecond> records : byNode.values()) {
            StorjSnoSecond latest = records.get(0);
            Long usedDiskSpace = latest.getUsedDiskSpace();
                StorjNode registeredNode = storjNodeRepository.findByNodeId(latest.getNodeId());
                Long availableDiskSpace = registeredNode == null ? null : registeredNode.getAvailableDiskSpace();
            Long totalDiskSpace = usedDiskSpace == null || availableDiskSpace == null
                ? null
                : usedDiskSpace + availableDiskSpace;
            nodeSummaries.add(new OverviewResponse.NodeOverviewResponse(
                latest.getNodeId(),
                latest.getUsedBandwidth(),
                usedDiskSpace,
                availableDiskSpace,
                totalDiskSpace
            ));
            if (!records.isEmpty() && records.get(0).getUsedBandwidth() != null) {
                current += records.get(0).getUsedBandwidth();
            }
            if (records.size() > 1 && records.get(1).getUsedBandwidth() != null) {
                previous += records.get(1).getUsedBandwidth();
            }
        }
        return new OverviewResponse(current, previous, current - previous, nodeSummaries);
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