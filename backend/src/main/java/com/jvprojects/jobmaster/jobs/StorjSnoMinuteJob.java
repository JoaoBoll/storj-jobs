package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.services.StorjSnoMinuteService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoMinuteJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoMinuteService storjSnoMinuteService;

    public StorjSnoMinuteJob(StorjSnoMinuteService storjSnoMinuteService) {
        this.storjSnoMinuteService = storjSnoMinuteService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Minute job...");

        storjSnoMinuteService.runJob();

        log.info("Finished.");
    }
}