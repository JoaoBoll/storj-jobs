package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.config.Configurations;
import com.jvprojects.jobmaster.dto.StorjSnoDto;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.services.sno.StorjSnoSecondService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StorjNodeService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoSecondService.class);
    private StorjSnoSecondService storjSnoSecondService;
    private StorjNodeRepository storjNodeRepository;
    private final Configurations configurations;
    private final List<String> urls;

    public StorjNodeService(Configurations configurations, StorjSnoSecondService storjSnoSecondService, StorjNodeRepository storjNodeRepository) {
        this.configurations = configurations;
        this.storjSnoSecondService = storjSnoSecondService;
        this.storjNodeRepository = storjNodeRepository;
        this.urls = configurations.getUrls();
    }

    public void executeOnStart() {
        log.info("Runnist startup task...");

        List<StorjSnoDto> storjSnosList = storjSnoSecondService.fetchStorjNodes();

        List<StorjNode> nodes = storjNodeRepository.findAll();

        for (StorjNode node : nodes) {
            Boolean enabled = storjSnosList.stream()
                    .anyMatch(sno -> sno.getNodeId().equals(node.getNodeId()));

            node.setEnabled(enabled);
        }

        AtomicInteger newNodeIndex = new AtomicInteger(nodes.size());

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
                        newStorjNode.setColor(generateNodeColor(newNodeIndex.getAndIncrement()));
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

    /**
     * Spreads hues by the golden angle (~137.5 deg) so consecutively registered nodes land as
     * far apart on the color wheel as possible, no matter how many nodes end up registering.
     * Saturation/lightness are pinned to a vivid, mid-bright band so no color drifts toward
     * black or white.
     */
    private String generateNodeColor(int index) {
        double hue = (index * 137.508) % 360;
        double saturation = 0.65 + (index % 3) * 0.08;
        double lightness = 0.55 + (index % 2) * 0.08;
        return hslToHex(hue, saturation, lightness);
    }

    private String hslToHex(double hue, double saturation, double lightness) {
        double c = (1 - Math.abs(2 * lightness - 1)) * saturation;
        double x = c * (1 - Math.abs((hue / 60.0) % 2 - 1));
        double m = lightness - c / 2;

        double r, g, b;
        if (hue < 60) { r = c; g = x; b = 0; }
        else if (hue < 120) { r = x; g = c; b = 0; }
        else if (hue < 180) { r = 0; g = c; b = x; }
        else if (hue < 240) { r = 0; g = x; b = c; }
        else if (hue < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        int red = Math.round((float) ((r + m) * 255));
        int green = Math.round((float) ((g + m) * 255));
        int blue = Math.round((float) ((b + m) * 255));
        return String.format("#%02x%02x%02x", red, green, blue);
    }

}
