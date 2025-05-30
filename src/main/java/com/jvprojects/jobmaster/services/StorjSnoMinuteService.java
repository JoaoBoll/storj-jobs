package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSno;
import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoMinuteRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSnoMinuteService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoRepository storjSnoRepository;
    private final StorjSnoMinuteRepository storjSnoMinuteRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoMinuteService(StorjSnoRepository storjSnoRepository, StorjSnoMinuteRepository storjSnoMinuteRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoRepository = storjSnoRepository;
        this.storjSnoMinuteRepository = storjSnoMinuteRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {

        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime startTime = now.minusMinutes(1)
                .withSecond(0)
                .withNano(0);

        OffsetDateTime endTime = now.withSecond(0)
                .withNano(0);

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {

            StorjSno first = storjSnoRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSno last = storjSnoRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

            if (!first.getId().equals(last.getId()) && first.getUsedBandwidth() != null
                    && last.getUsedBandwidth() != null && durationInSeconds != null) {

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = totalUsedBandwidth / durationInSeconds;

                StorjSnoMinute minute = new StorjSnoMinute();
                minute.setNodeId(last.getNodeId());
                minute.setUsedDiskSpace(last.getUsedDiskSpace());
                minute.setTrashDiskSpace(last.getTrashDiskSpace());
                minute.setUsedBandwidth(last.getUsedBandwidth());
                minute.setOverusedDiskSpace(last.getOverusedDiskSpace());
                minute.setTotalUsedBandwidth(totalUsedBandwidth);
                minute.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);

                storjSnoMinuteRepository.save(minute);
            }
        }
    }
}