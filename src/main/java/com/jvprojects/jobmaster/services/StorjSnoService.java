package com.jvprojects.jobmaster.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSnoDto;
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

    private final Configurations configurations;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> urls;

    public StorjSnoService(Configurations configurations) {
        this.configurations = configurations;
        this.urls = configurations.getUrls();
    }

    public List<StorjSnoDto> fetchStorjNodes() {
        log.info("Fetching Storj Nodes data...");
        List<CompletableFuture<StorjSnoDto>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> consultarNode(url)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    private StorjSnoDto consultarNode(String url) {
        try {
            String json = restTemplate.getForObject(url + "/api/sno/", String.class);
            StorjSnoDto dto = objectMapper.readValue(json, StorjSnoDto.class);
            log.info("✅ Success: {}", url);
            return dto;
        } catch (Exception e) {
            log.error("❌ Fail {}: {}", url, e.getMessage(), e);
            return null;
        }
    }
}