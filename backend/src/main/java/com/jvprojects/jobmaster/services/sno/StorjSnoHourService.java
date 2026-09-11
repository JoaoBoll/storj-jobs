package com.jvprojects.jobmaster.services.sno;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoHour;
import com.jvprojects.jobmaster.entities.StorjSno30m;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoHourRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno30mRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSnoHourService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoHourService.class);

    private final StorjSno30mRepository storjSno30mRepository;
    private final StorjSnoHourRepository storjSnoHourRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoHourService(StorjSno30mRepository storjSno30mRepository, StorjSnoHourRepository storjSnoHourRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSno30mRepository = storjSno30mRepository;
        this.storjSnoHourRepository = storjSnoHourRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {

        OffsetDateTime now = OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0);

        OffsetDateTime startTime = now.minusHours(1);
        OffsetDateTime endTime = now;

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {

            StorjSno30m first = storjSno30mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSno30m last = storjSno30mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (first == null) {
                first = storjSno30mRepository.findFirstByNodeIdOrderByCreatedAtAsc(storjNode.getNodeId());
            }

            if (last == null) {
                last = storjSno30mRepository.findFirstByNodeIdOrderByCreatedAtDesc(storjNode.getNodeId());
            }

            if (first != null && last != null && first.getUsedBandwidth() != null
                    && last.getUsedBandwidth() != null && !first.getId().equals(last.getId())) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = durationInSeconds > 0 ? totalUsedBandwidth / durationInSeconds : 0;

                StorjSnoHour hour = new StorjSnoHour();
                hour.setNodeId(last.getNodeId());
                hour.setUsedDiskSpace(last.getUsedDiskSpace());
                hour.setTrashDiskSpace(last.getTrashDiskSpace());
                hour.setUsedBandwidth(last.getUsedBandwidth());
                hour.setOverusedDiskSpace(last.getOverusedDiskSpace());
                hour.setTotalUsedBandwidth(totalUsedBandwidth);
                hour.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);
                hour.setIngressTotal(last.getIngressTotal());
                hour.setEgressTotal(last.getEgressTotal());
                hour.setUptimeAverage(last.getUptimeAverage());

                storjSnoHourRepository.save(hour);
            }
        }
    }
}