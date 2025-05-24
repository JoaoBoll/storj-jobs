package com.jvprojects.jobmaster.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSnoDto;
import com.jvprojects.jobmaster.entities.StorjSno;
import com.jvprojects.jobmaster.repositories.StorjSnoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StorjSnoService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final StorjSnoRepository storjSnoRepository;
    private final Configurations configurations;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> urls;

    public StorjSnoService(StorjSnoRepository storjSnoRepository,Configurations configurations) {
        this.storjSnoRepository = storjSnoRepository;
        this.configurations = configurations;
        this.urls = configurations.getUrls();
    }

    public List<StorjSnoDto> fetchStorjNodes() {
        log.info("Fetching Storj Satellites data...");
        List<CompletableFuture<StorjSnoDto>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> consultNode(url)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    private StorjSnoDto consultNode(String url) {
        try {
            String json = restTemplate.getForObject(url + "/api/sno/", String.class);
            StorjSnoDto dto = objectMapper.readValue(json, StorjSnoDto.class);
            dto.setUrl(url);
            log.info("✅ Success: {}", url);
            return dto;
        } catch (Exception e) {
            log.error("❌ Fail {}: {}", url, e.getMessage(), e);
            return null;
        }
    }

    public void saveAll(List<StorjSnoDto> itens) {
        for (StorjSnoDto item : itens) {
            StorjSno storjSno = new StorjSno();

            if (item.getNodeId() != null) {
                storjSno.setNodeId(item.getNodeId());
            }

            if (item.getDiskSpace() != null) {
                storjSno.setUsedDiskSpace(item.getDiskSpace().getUsed());
                storjSno.setTrash(item.getDiskSpace().getTrash());
            }

            if (item.getBandwidth() != null) {
                storjSno.setBandwidth(item.getBandwidth().getUsed());
            }
            storjSnoRepository.save(storjSno);
        }
    }
}