package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSnoHourService;
import com.jvprojects.jobmaster.services.sno.StorjSnoMinuteService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoHourJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoHourService storjSnoHourService;

    public StorjSnoHourJob(StorjSnoHourService storjSnoHourService) {
        this.storjSnoHourService = storjSnoHourService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Hour job...");

        storjSnoHourService.runJob();

        log.info("Finished.");
    }
}