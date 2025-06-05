package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoMonth;
import com.jvprojects.jobmaster.entities.StorjSnoDay;
import com.jvprojects.jobmaster.entities.StorjSnoWeek;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoDayRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoWeekRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoWeekRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class StorjSnoWeekService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoWeekService.class);

    private final StorjSnoDayRepository storjSnoDayRepository;
    private final StorjSnoWeekRepository storjSnoWeekRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoWeekService(StorjSnoDayRepository storjSnoDayRepository, StorjSnoWeekRepository storjSnoWeekRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoDayRepository = storjSnoDayRepository;
        this.storjSnoWeekRepository = storjSnoWeekRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {

        OffsetDateTime now = OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0);

        OffsetDateTime startTime = now.minusDays(7);
        OffsetDateTime endTime = now;

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {

            StorjSnoDay first = storjSnoDayRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSnoDay last = storjSnoDayRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (!first.getId().equals(last.getId()) && first.getUsedBandwidth() != null
                    && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = totalUsedBandwidth / durationInSeconds;

                StorjSnoWeek week = new StorjSnoWeek();
                week.setNodeId(last.getNodeId());
                week.setUsedDiskSpace(last.getUsedDiskSpace());
                week.setTrashDiskSpace(last.getTrashDiskSpace());
                week.setUsedBandwidth(last.getUsedBandwidth());
                week.setOverusedDiskSpace(last.getOverusedDiskSpace());
                week.setTotalUsedBandwidth(totalUsedBandwidth);
                week.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);

                storjSnoWeekRepository.save(week);
            }
        }
    }
}