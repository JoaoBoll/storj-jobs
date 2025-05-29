package com.jvprojects.jobmaster.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.entities.StorjSatellites;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSatellitesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class StorjHourService {

    private static final Logger log = LoggerFactory.getLogger(StorjHourService.class);

    private StorjNodeRepository storjNodeRepository;
    private final StorjSatellitesRepository storjSatellitesRepository;
    private final Configurations configurations;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final List<String> urls;

    public StorjHourService(StorjSatellitesRepository storjSatellitesRepository, Configurations configurations) {
        this.storjSatellitesRepository = storjSatellitesRepository;
        this.configurations = configurations;
        this.urls = configurations.getUrls();
    }

    public void processJob() {

        // Current date-time in UTC-0
        OffsetDateTime currentUtcTime = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime oneHourAgo = currentUtcTime.minusHours(1);

        System.out.println("Current UTC Time: " + currentUtcTime);
        System.out.println("One Hour Ago UTC: " + oneHourAgo);

        List<StorjSatellites> storjNodes = storjSatellitesRepository.findByCreatedAtBetween(oneHourAgo, currentUtcTime);


    }

}