package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSno;
import com.jvprojects.jobmaster.entities.StorjSnoMinutes;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoMinutesRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoSecondsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSnoMinuteService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoSecondsRepository storjSnoSecondsRepository;
    private final StorjSnoMinutesRepository storjSnoMinutesRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoMinuteService(StorjSnoSecondsRepository storjSnoSecondsRepository, StorjSnoMinutesRepository storjSnoMinutesRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoSecondsRepository = storjSnoSecondsRepository;
        this.storjSnoMinutesRepository = storjSnoMinutesRepository;
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

            StorjSno first = storjSnoSecondsRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSno last = storjSnoSecondsRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

            if (!first.getId().equals(last.getId()) && first.getUsedBandwidth() != null
                    && last.getUsedBandwidth() != null && durationInSeconds != null) {

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = totalUsedBandwidth / durationInSeconds;

                StorjSnoMinutes minute = new StorjSnoMinutes();
                minute.setNodeId(last.getNodeId());
                minute.setUsedDiskSpace(last.getUsedDiskSpace());
                minute.setTrashDiskSpace(last.getTrashDiskSpace());
                minute.setUsedBandwidth(last.getUsedBandwidth());
                minute.setOverusedDiskSpace(last.getOverusedDiskSpace());
                minute.setTotalUsedBandwidth(totalUsedBandwidth);
                minute.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);

                storjSnoMinutesRepository.save(minute);
            }
        }
    }
}