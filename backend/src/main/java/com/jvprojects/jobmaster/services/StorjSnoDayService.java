package com.jvprojects.jobmaster.services;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoDay;
import com.jvprojects.jobmaster.entities.StorjSnoHour;
import com.jvprojects.jobmaster.entities.common.StorjSno;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoDayRepository;
import com.jvprojects.jobmaster.repositories.StorjSnoHourRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSnoDayService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoDayService.class);

    private final StorjSnoHourRepository storjSnoHourRepository;
    private final StorjSnoDayRepository storjSnoDayRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoDayService(StorjSnoHourRepository storjSnoHourRepository, StorjSnoDayRepository storjSnoDayRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoHourRepository = storjSnoHourRepository;
        this.storjSnoDayRepository = storjSnoDayRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {

        OffsetDateTime now = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        OffsetDateTime startTime = now.minusDays(1);
        OffsetDateTime endTime = now;

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {

            StorjSnoHour first = storjSnoHourRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSnoHour last = storjSnoHourRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (!first.getId().equals(last.getId()) && first.getUsedBandwidth() != null
                    && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = totalUsedBandwidth / durationInSeconds;

                StorjSnoDay day = new StorjSnoDay();
                day.setNodeId(last.getNodeId());
                day.setUsedDiskSpace(last.getUsedDiskSpace());
                day.setTrashDiskSpace(last.getTrashDiskSpace());
                day.setUsedBandwidth(last.getUsedBandwidth());
                day.setOverusedDiskSpace(last.getOverusedDiskSpace());
                day.setTotalUsedBandwidth(totalUsedBandwidth);
                day.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);

                storjSnoDayRepository.save(day);
            }
        }
    }
}