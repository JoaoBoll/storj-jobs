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

        for (StorjSnoDto storjSnoDto : storjSnosList) {
            StorjNode actualNode = storjNodeRepository.findByNodeId(storjSnoDto.getNodeId());
            if (actualNode == null) {
                StorjNode node = new StorjNode();

                node.setNodeId(storjSnoDto.getNodeId());
                node.setUrl(storjSnoDto.getUrl());

                storjNodeRepository.save(node);
            } else {
                if (!storjSnoDto.getUrl().equals(actualNode.getUrl())) {
                    actualNode.setUrl(storjSnoDto.getUrl());
                    storjNodeRepository.save(actualNode);
                }
            }
        }

        log.info("Finished");
    }

}
