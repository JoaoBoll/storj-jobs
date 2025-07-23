package com.jvprojects.jobmaster.services.sno;

import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.entities.StorjSnoMonth;
import com.jvprojects.jobmaster.entities.StorjSnoWeek;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoMonthRepository;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoWeekRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class StorjSnoMonthService {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMonthService.class);

    private final StorjSnoWeekRepository storjSnoWeekRepository;
    private final StorjSnoMonthRepository storjSnoMonthRepository;
    private final StorjNodeRepository storjNodeRepository;

    public StorjSnoMonthService(StorjSnoWeekRepository storjSnoWeekRepository, StorjSnoMonthRepository storjSnoMonthRepository, StorjNodeRepository storjNodeRepository) {
        this.storjSnoWeekRepository = storjSnoWeekRepository;
        this.storjSnoMonthRepository = storjSnoMonthRepository;
        this.storjNodeRepository = storjNodeRepository;
    }

    public void runJob() {

        OffsetDateTime now = OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0);

        OffsetDateTime startTime = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        OffsetDateTime endTime = now.with(TemporalAdjusters.firstDayOfMonth());

        List<StorjNode> storjNodes = storjNodeRepository.findAllByEnabledIsTrue();

        for (StorjNode storjNode : storjNodes) {

            StorjSnoWeek first = storjSnoWeekRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(storjNode.getNodeId(), startTime, endTime);
            StorjSnoWeek last = storjSnoWeekRepository.findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(storjNode.getNodeId(), startTime, endTime);

            if (first == null) {
                first = storjSnoWeekRepository.findFirstByNodeIdOrderByCreatedAtAsc(storjNode.getNodeId());
            }

            if (last == null) {
                last = storjSnoWeekRepository.findFirstByNodeIdOrderByCreatedAtDesc(storjNode.getNodeId());
            }

            if (first != null && last != null && !first.getId().equals(last.getId())
                    && first.getUsedBandwidth() != null && last.getUsedBandwidth() != null) {

                Long durationInSeconds = java.time.Duration.between(first.getCreatedAt(), last.getCreatedAt()).getSeconds();

                Long totalUsedBandwidth = last.getUsedBandwidth() - first.getUsedBandwidth();
                Long totalConsumeBandwidthPerSecond = totalUsedBandwidth / durationInSeconds;

                StorjSnoMonth month = new StorjSnoMonth();
                month.setNodeId(last.getNodeId());
                month.setUsedDiskSpace(last.getUsedDiskSpace());
                month.setTrashDiskSpace(last.getTrashDiskSpace());
                month.setUsedBandwidth(last.getUsedBandwidth());
                month.setOverusedDiskSpace(last.getOverusedDiskSpace());
                month.setTotalUsedBandwidth(totalUsedBandwidth);
                month.setTotalConsumeBandwidthPerSecond(totalConsumeBandwidthPerSecond);

                storjSnoMonthRepository.save(month);
            }
        }
    }
}