package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSno5mService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSno5mJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSno5mJob.class);
    private final StorjSno5mService storjSno5mService;

    public StorjSno5mJob(StorjSno5mService storjSno5mService) {
        this.storjSno5mService = storjSno5mService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO 5m job...");
        storjSno5mService.runJob();
        log.info("Finished.");
    }
}
