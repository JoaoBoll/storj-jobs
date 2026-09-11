package com.jvprojects.jobmaster.initialization;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.util.List;
import java.util.Random;

@Component
public class StorjNodeColorInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StorjNodeColorInitializer.class);
    private final StorjNodeRepository storjNodeRepository;
    private final Random random = new Random();

    private static String generateColor() {
        Random random = new Random();

        float hue = random.nextFloat() * 360f;
        float saturation = 0.55f + random.nextFloat() * 0.35f;
        float lightness = 0.60f + random.nextFloat() * 0.20f;

        return String.format(
            "#%02x%02x%02x",
            Color.HSBtoRGB(hue / 360f, saturation, lightness) >> 16 & 0xff,
            Color.HSBtoRGB(hue / 360f, saturation, lightness) >> 8 & 0xff,
            Color.HSBtoRGB(hue / 360f, saturation, lightness) & 0xff
        );
    }

    public StorjNodeColorInitializer(StorjNodeRepository storjNodeRepository) {
        this.storjNodeRepository = storjNodeRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<StorjNode> nodesWithoutColor = storjNodeRepository.findAll().stream()
                .filter(node -> node.getColor() == null || node.getColor().isBlank())
                .toList();

        if (nodesWithoutColor.isEmpty()) {
            log.info("✓ All nodes already have colors assigned");
            return;
        }

        log.info("🎨 Assigning colors to {} nodes without color...", nodesWithoutColor.size());

        nodesWithoutColor.forEach(node -> {
            String color = generateColor();
            node.setColor(color);
            storjNodeRepository.save(node);
            log.info("  → Node {} assigned color {}", node.getNodeId(), color);
        });

        log.info("✓ Color assignment complete!");
    }
}
