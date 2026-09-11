package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSno30mService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSno30mJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSno30mJob.class);
    private final StorjSno30mService storjSno30mService;

    public StorjSno30mJob(StorjSno30mService storjSno30mService) {
        this.storjSno30mService = storjSno30mService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO 30m job...");
        storjSno30mService.runJob();
        log.info("Finished.");
    }
}
