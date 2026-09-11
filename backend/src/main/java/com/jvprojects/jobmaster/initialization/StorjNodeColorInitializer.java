package com.jvprojects.jobmaster.initialization;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Component
public class StorjNodeColorInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StorjNodeColorInitializer.class);
    private final StorjNodeRepository storjNodeRepository;
    private final Random random = new Random();

    // Palette of distinct colors for nodes
    private static final String[] COLOR_PALETTE = {
        "#83a9ff", "#c7f36b", "#f4bb61", "#5bd6e8",
        "#ff6b6b", "#4ecdc4", "#45b7d1", "#f7b731",
        "#5f27cd", "#00d2d3", "#ff9ff3", "#54a0ff",
        "#48dbfb", "#1dd1a1", "#ff6348", "#a55eea"
    };

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
            String color = COLOR_PALETTE[random.nextInt(COLOR_PALETTE.length)];
            node.setColor(color);
            storjNodeRepository.save(node);
            log.info("  → Node {} assigned color {}", node.getNodeId(), color);
        });

        log.info("✓ Color assignment complete!");
    }
}
