package com.jvprojects.jobmaster.services.sno;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import com.jvprojects.jobmaster.entities.StorjSno5m;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoMinuteRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno5mRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSno5mService {

    private static final Logger log = LoggerFactory.getLogger(StorjSno5mService.class);

    private final StorjSnoMinuteRepository storjSnoMinuteRepository;
    private final StorjSno5mRepository storjSno5mRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSno5mService(StorjSnoMinuteRepository storjSnoMinuteRepository, StorjSno5mRepository storjSno5mRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoMinuteRepository = storjSnoMinuteRepository;
        this.storjSno5mRepository = storjSno5mRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startTime = now.minusMinutes(5).withSecond(0).withNano(0);
        OffsetDateTime endTime = now.withSecond(0).withNano(0);

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {
            StorjSnoMinute first = storjSnoMinuteRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSnoMinute last = storjSnoMinuteRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (first == null) first = storjSnoMinuteRepository.findFirstByNodeIdOrderByCreatedAtAsc(storjNode.getNodeId());
            if (last == null) last = storjSnoMinuteRepository.findFirstByNodeIdOrderByCreatedAtDesc(storjNode.getNodeId());

            if (first != null && last != null && !first.getId().equals(last.getId())
                    && first.getUsedBandwidth() != null && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();
                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = durationInSeconds > 0 ? totalUsedBandwidth / durationInSeconds : 0;

                StorjSno5m data = new StorjSno5m();
                data.setNodeId(last.getNodeId());
                data.setUsedDiskSpace(last.getUsedDiskSpace());
                data.setTrashDiskSpace(last.getTrashDiskSpace());
                data.setUsedBandwidth(last.getUsedBandwidth());
                data.setOverusedDiskSpace(last.getOverusedDiskSpace());
                data.setTotalUsedBandwidth(totalUsedBandwidth);
                data.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);
                data.setIngressTotal(last.getIngressTotal());
                data.setEgressTotal(last.getEgressTotal());
                data.setUptimeScoreSum(last.getUptimeScoreSum());
                data.setUptimeScoreCount(last.getUptimeScoreCount());

                storjSno5mRepository.save(data);
            }
        }
    }
}
