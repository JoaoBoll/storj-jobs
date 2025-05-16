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
            if (storjNodeRepository.findByNodeId(storjSnoDto.getNodeId()) == null) {
                StorjNode node = new StorjNode();

                node.setNodeId(storjSnoDto.getNodeId());

                storjNodeRepository.save(node);
            }
        }

        log.info("Finished");
    }

}
