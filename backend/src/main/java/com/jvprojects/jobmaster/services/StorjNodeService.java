package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSnoDto;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorjNodeService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);
    private StorjSnoService storjSnoService;
    private StorjNodeRepository storjNodeRepository;
    private final Configurations configurations;
    private final List<String> urls;

    public StorjNodeService(Configurations configurations, StorjSnoService storjSnoService, StorjNodeRepository storjNodeRepository) {
        this.configurations = configurations;
        this.storjSnoService = storjSnoService;
        this.storjNodeRepository = storjNodeRepository;
        this.urls = configurations.getUrls();
    }

    public void executeOnStart() {
        log.info("Runnist startup task...");

        List<StorjSnoDto> storjSnosList = storjSnoService.fetchStorjNodes();

        List<StorjNode> nodes = storjNodeRepository.findAll();

        for (StorjNode node : nodes) {
            Boolean enabled = storjSnosList.stream()
                    .anyMatch(sno -> sno.getNodeId().equals(node.getNodeId()));

            node.setEnabled(enabled);
        }

        for (StorjSnoDto storjSnoDto : storjSnosList) {
            StorjNode node = nodes.stream()
                    .filter(n -> n.getNodeId().equals(storjSnoDto.getNodeId()))
                    .findFirst()
                    .orElseGet(() -> {
                        StorjNode newStorjNode = new StorjNode();
                        newStorjNode.setNodeId(storjSnoDto.getNodeId());
                        newStorjNode.setUrl(storjSnoDto.getUrl());
                        newStorjNode.setAvailableDiskSpace(storjSnoDto.getDiskSpace().getAvailable());
                        newStorjNode.setEnabled(true); //Assuming the new node should be enabled
                        return newStorjNode;
                    });

            if (node.getId() != null) {
                if (!storjSnoDto.getUrl().equals(node.getUrl())) {
                    node.setUrl(storjSnoDto.getUrl());
                }
                if (!storjSnoDto.getDiskSpace().getAvailable().equals(node.getAvailableDiskSpace())) {
                    node.setAvailableDiskSpace(storjSnoDto.getDiskSpace().getAvailable());
                }
            }
            storjNodeRepository.save(node);
        }
    }

}
