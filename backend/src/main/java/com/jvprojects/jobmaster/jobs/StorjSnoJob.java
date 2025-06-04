package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.services.StorjSnoService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final StorjSnoService storjSnoService;

    public StorjSnoJob(StorjSnoService storjSnoService) {
        this.storjSnoService = storjSnoService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Searching for Storj Node Operators (SNO)...");

        storjSnoService.runJob();

        log.info("Finished.");
    }
}