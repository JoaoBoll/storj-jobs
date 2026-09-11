package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSno15mService;
import com.jvprojects.jobmaster.services.sno.StorjSno30mService;
import com.jvprojects.jobmaster.services.sno.StorjSno5mService;
import com.jvprojects.jobmaster.services.sno.StorjSnoDayService;
import com.jvprojects.jobmaster.services.sno.StorjSnoHourService;
import com.jvprojects.jobmaster.services.sno.StorjSnoMinuteService;
import com.jvprojects.jobmaster.services.sno.StorjSnoMonthService;
import com.jvprojects.jobmaster.services.sno.StorjSnoSecondService;
import com.jvprojects.jobmaster.services.sno.StorjSnoWeekService;
import com.jvprojects.jobmaster.services.satellites.StorjEstimatedPayoutService;
import com.jvprojects.jobmaster.services.satellites.StorjSatellitesService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;

/**
 * Fetches the live snapshot from the Storj node API and, in the same run, cascades
 * every DB-only aggregation level (minute -> 5m -> 15m -> 30m -> hour -> day -> week/month)
 * whose time boundary has just been reached, so nothing is scheduled independently
 * and every level always aggregates from freshly written data.
 */
@DisallowConcurrentExecution
public class StorjSnoCollectorJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoCollectorJob.class);

    private final StorjSnoSecondService storjSnoSecondService;
    private final StorjSatellitesService storjSatellitesService;
    private final StorjEstimatedPayoutService storjEstimatedPayoutService;
    private final StorjSnoMinuteService storjSnoMinuteService;
    private final StorjSno5mService storjSno5mService;
    private final StorjSno15mService storjSno15mService;
    private final StorjSno30mService storjSno30mService;
    private final StorjSnoHourService storjSnoHourService;
    private final StorjSnoDayService storjSnoDayService;
    private final StorjSnoWeekService storjSnoWeekService;
    private final StorjSnoMonthService storjSnoMonthService;

    public StorjSnoCollectorJob(StorjSnoSecondService storjSnoSecondService,
                                 StorjSatellitesService storjSatellitesService,
                                 StorjEstimatedPayoutService storjEstimatedPayoutService,
                                 StorjSnoMinuteService storjSnoMinuteService,
                                 StorjSno5mService storjSno5mService,
                                 StorjSno15mService storjSno15mService,
                                 StorjSno30mService storjSno30mService,
                                 StorjSnoHourService storjSnoHourService,
                                 StorjSnoDayService storjSnoDayService,
                                 StorjSnoWeekService storjSnoWeekService,
                                 StorjSnoMonthService storjSnoMonthService) {
        this.storjSnoSecondService = storjSnoSecondService;
        this.storjSatellitesService = storjSatellitesService;
        this.storjEstimatedPayoutService = storjEstimatedPayoutService;
        this.storjSnoMinuteService = storjSnoMinuteService;
        this.storjSno5mService = storjSno5mService;
        this.storjSno15mService = storjSno15mService;
        this.storjSno30mService = storjSno30mService;
        this.storjSnoHourService = storjSnoHourService;
        this.storjSnoDayService = storjSnoDayService;
        this.storjSnoWeekService = storjSnoWeekService;
        this.storjSnoMonthService = storjSnoMonthService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Collecting Storj SNO snapshot...");
        storjSnoSecondService.runJob();
        cascadeAggregations(OffsetDateTime.now());
        log.info("Finished.");
    }

    private void cascadeAggregations(OffsetDateTime now) {
        if (now.getSecond() != 0) {
            return;
        }
        storjSatellitesService.saveAll(storjSatellitesService.fetchStorjSatellites());
        storjEstimatedPayoutService.saveAll(storjEstimatedPayoutService.fetchEstimatedPayouts());
        storjSnoMinuteService.runJob();

        int minute = now.getMinute();
        if (minute % 5 != 0) {
            return;
        }
        storjSno5mService.runJob();

        if (minute % 15 != 0) {
            return;
        }
        storjSno15mService.runJob();

        if (minute % 30 != 0) {
            return;
        }
        storjSno30mService.runJob();

        if (minute != 0) {
            return;
        }
        storjSnoHourService.runJob();

        if (now.getHour() != 0) {
            return;
        }
        storjSnoDayService.runJob();

        if (now.getDayOfWeek() == DayOfWeek.SUNDAY) {
            storjSnoWeekService.runJob();
        }
        if (now.getDayOfMonth() == 1) {
            storjSnoMonthService.runJob();
        }
    }
}
