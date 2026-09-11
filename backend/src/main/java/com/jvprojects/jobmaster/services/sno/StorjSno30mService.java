package com.jvprojects.jobmaster.services.sno;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSno15m;
import com.jvprojects.jobmaster.entities.StorjSno30m;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno15mRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSno30mRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StorjSno30mService {

    private static final Logger log = LoggerFactory.getLogger(StorjSno30mService.class);

    private final StorjSno15mRepository storjSno15mRepository;
    private final StorjSno30mRepository storjSno30mRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSno30mService(StorjSno15mRepository storjSno15mRepository, StorjSno30mRepository storjSno30mRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSno15mRepository = storjSno15mRepository;
        this.storjSno30mRepository = storjSno30mRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startTime = now.minusMinutes(30).withSecond(0).withNano(0);
        OffsetDateTime endTime = now.withSecond(0).withNano(0);

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {
            StorjSno15m first = storjSno15mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSno15m last = storjSno15mRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (first == null) first = storjSno15mRepository.findFirstByNodeIdOrderByCreatedAtAsc(storjNode.getNodeId());
            if (last == null) last = storjSno15mRepository.findFirstByNodeIdOrderByCreatedAtDesc(storjNode.getNodeId());

            if (first != null && last != null && !first.getId().equals(last.getId())
                    && first.getUsedBandwidth() != null && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();
                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = durationInSeconds > 0 ? totalUsedBandwidth / durationInSeconds : 0;

                StorjSno30m data = new StorjSno30m();
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

                storjSno30mRepository.save(data);
            }
        }
    }
}
