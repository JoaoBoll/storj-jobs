package com.jvprojects.jobmaster.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSnoDto;
import com.jvprojects.jobmaster.entities.StorjSnoSecond;
import com.jvprojects.jobmaster.repositories.StorjSnoSecondRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StorjSnoSecondService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoSecondService.class);

    private final StorjSnoSecondRepository storjSnoSecondRepository;
    private final Configurations configurations;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> urls;

    public StorjSnoSecondService(StorjSnoSecondRepository storjSnoSecondRepository, Configurations configurations) {
        this.storjSnoSecondRepository = storjSnoSecondRepository;
        this.configurations = configurations;
        this.urls = configurations.getUrls();
    }

    public void runJob() {

        List<StorjSnoDto> items = fetchStorjNodes();
        saveAll(items);

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

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        log.info("Current time in UTC: " + now);

        for (StorjSnoDto item : itens) {
            StorjSnoSecond second = new StorjSnoSecond();

            second.setCreatedAt(now);
            if (item.getNodeId() != null) {
                second.setNodeId(item.getNodeId());
            }

            if (item.getDiskSpace() != null) {
                second.setUsedDiskSpace(item.getDiskSpace().getUsed());
                second.setTrashDiskSpace(item.getDiskSpace().getTrash());
                second.setOverusedDiskSpace(item.getDiskSpace().getOverused());
            }

            if (item.getBandwidth() != null) {
                second.setUsedBandwidth(item.getBandwidth().getUsed());
            }
            storjSnoSecondRepository.save(second);
        }
    }
}