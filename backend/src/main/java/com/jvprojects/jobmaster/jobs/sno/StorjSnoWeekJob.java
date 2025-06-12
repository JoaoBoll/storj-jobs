package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSnoMinuteService;
import com.jvprojects.jobmaster.services.sno.StorjSnoWeekService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoWeekJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoWeekService storjSnoWeekService;

    public StorjSnoWeekJob(StorjSnoWeekService storjSnoWeekService) {
        this.storjSnoWeekService = storjSnoWeekService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Week job...");

        storjSnoWeekService.runJob();

        log.info("Finished.");
    }
}