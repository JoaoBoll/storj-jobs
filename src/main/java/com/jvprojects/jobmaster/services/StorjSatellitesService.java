package com.jvprojects.jobmaster.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSatellitesDto;
import com.jvprojects.jobmaster.entities.*;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSatellitesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StorjSatellitesService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final Configurations configurations;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> urls;

    private StorjNodeRepository storjNodeRepository;
    private StorjSatellitesRepository storjSatellitesRepository;

    public StorjSatellitesService(Configurations configurations, StorjNodeRepository storjNodeRepository, StorjSatellitesRepository storjSatellitesRepository) {
        this.configurations = configurations;
        this.urls = configurations.getUrls();
        this.storjNodeRepository = storjNodeRepository;
        this.storjSatellitesRepository = storjSatellitesRepository;
    }

    public List<StorjSatellitesDto> fetchStorjSatellites() {
        log.info("Fetching Storj Satellites data...");
        List<CompletableFuture<StorjSatellitesDto>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> consultNode(url)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    private StorjSatellitesDto consultNode(String url) {
        try {
            String json = restTemplate.getForObject(url + "/api/sno/satellites", String.class);
            StorjSatellitesDto dto = objectMapper.readValue(json, StorjSatellitesDto.class);
            dto.setUlr(url);
            log.info("✅ Success: {}", url);
            return dto;
        } catch (Exception e) {
            log.error("❌ Fail {}: {}", url, e.getMessage(), e);
            return null;
        }
    }

    public void saveAll(List<StorjSatellitesDto> itens) {

        for (StorjSatellitesDto item : itens) {

            StorjNode storjNode = storjNodeRepository.findByUrl(item.getUlr());

            StorjSatellites satellites = storjSatellitesRepository.findByStorjNodeId(storjNode.getId());

            if (satellites == null) {
                satellites = new StorjSatellites();
                satellites.setStorjNode(storjNode);
            }

            //StorageDaily
            List<StorageDaily> storageDailyList = new ArrayList<>();

            for (StorjSatellitesDto.StorageDaily daily : item.getStorageDaily()) {
                StorageDaily storageDaily = new StorageDaily();

                storageDaily.setAtRestTotal(daily.getAtRestTotal());
                storageDaily.setAtRestTotalBytes(daily.getAtRestTotalBytes());
                storageDaily.setIntervalStart(OffsetDateTime.parse(daily.getIntervalStart()));

                storageDaily.setStorjSatellite(satellites);

                storageDailyList.add(storageDaily);
            }

            satellites.setStorageDaily(storageDailyList);

            //BandwidthDaily
            List<BandwidthDaily> bandwidthDailyList = new ArrayList<>();

            for (StorjSatellitesDto.BandwidthDaily band : item.getBandwidthDaily()) {
                BandwidthDaily bandwidthDaily = new BandwidthDaily(band);

                bandwidthDaily.setStorjSatellite(satellites);
                bandwidthDailyList.add(bandwidthDaily);
            }

            satellites.setBandwidthDaily(bandwidthDailyList);

            //Audits
            List<Audits> audits = new ArrayList<>();

            for (StorjSatellitesDto.Audit audit : item.getAudits()) {
                Audits aud =  new Audits(audit);

                aud.setStorjSatellite(satellites);
                audits.add(aud);
            }

            satellites.setAudits(audits);

            satellites.setStorageSummary(item.getStorageSummary());
            satellites.setAverageUsageBytes(item.getAverageUsageBytes());
            satellites.setBandwidthSummary(item.getBandwidthSummary());
            satellites.setEgressSummary(item.getEgressSummary());
            satellites.setIngressSummary(item.getIngressSummary());
            satellites.setEarliestJoinedAt(OffsetDateTime.parse(item.getEarliestJoinedAt()));

            storjSatellitesRepository.save(satellites);
        }

    }
}
