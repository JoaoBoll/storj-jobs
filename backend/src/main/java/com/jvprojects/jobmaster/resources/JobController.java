package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.dto.StorjNodeResponse;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoSecondRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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