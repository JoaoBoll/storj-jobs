package com.jvprojects.jobmaster.services.sno;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSno5m;
import com.jvprojects.jobmaster.entities.StorjSno15m;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno5mRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno15mRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSno15mService {

    private static final Logger log = LoggerFactory.getLogger(StorjSno15mService.class);

    private final StorjSno5mRepository storjSno5mRepository;
    private final StorjSno15mRepository storjSno15mRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSno15mService(StorjSno5mRepository storjSno5mRepository, StorjSno15mRepository storjSno15mRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSno5mRepository = storjSno5mRepository;
        this.storjSno15mRepository = storjSno15mRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startTime = now.minusMinutes(15).withSecond(0).withNano(0);
        OffsetDateTime endTime = now.withSecond(0).withNano(0);

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {
            StorjSno5m first = storjSno5mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSno5m last = storjSno5mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (first == null) first = storjSno5mRepository.findFirstByNodeIdOrderByCreatedAtAsc(storjNode.getNodeId());
            if (last == null) last = storjSno5mRepository.findFirstByNodeIdOrderByCreatedAtDesc(storjNode.getNodeId());

            if (first != null && last != null && !first.getId().equals(last.getId())
                    && first.getUsedBandwidth() != null && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();
                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = durationInSeconds > 0 ? totalUsedBandwidth / durationInSeconds : 0;

                StorjSno15m data = new StorjSno15m();
                data.setNodeId(last.getNodeId());
                data.setUsedDiskSpace(last.getUsedDiskSpace());
                data.setTrashDiskSpace(last.getTrashDiskSpace());
                data.setUsedBandwidth(last.getUsedBandwidth());
                data.setOverusedDiskSpace(last.getOverusedDiskSpace());
                data.setTotalUsedBandwidth(totalUsedBandwidth);
                data.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);
                data.setIngressTotal(last.getIngressTotal());
                data.setEgressTotal(last.getEgressTotal());
                data.setUptimeAverage(last.getUptimeAverage());

                storjSno15mRepository.save(data);
            }
        }
    }
}
