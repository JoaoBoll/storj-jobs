package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.dto.StorjNodeResponse;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
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

    public JobController(StorjNodeRepository storjNodeRepository) {
        this.storjNodeRepository = storjNodeRepository;
    }

    @GetMapping("/nodes")
    public List<StorjNodeResponse> nodes() {
        return storjNodeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private StorjNodeResponse toResponse(StorjNode node) {
        return new StorjNodeResponse(
                node.getId(),
                node.getNodeId(),
                node.getUrl(),
                node.getEnabled(),
                node.getAvailableDiskSpace(),
                node.getCreatedAt(),
                node.getUpdatedAt()
        );
    }

}