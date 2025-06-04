package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.services.StorjHourService;
import com.jvprojects.jobmaster.services.StorjSnoService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjHourlyJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final StorjHourService storjHourService;

    public StorjHourlyJob(StorjHourService storjHourService) {
        this.storjHourService = storjHourService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Performing Hourly Job...");

        storjHourService.processJob();

        log.info("Finished.");
    }

}
