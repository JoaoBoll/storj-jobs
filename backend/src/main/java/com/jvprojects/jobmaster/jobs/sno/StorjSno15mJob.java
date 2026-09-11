package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSno15mService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSno15mJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSno15mJob.class);
    private final StorjSno15mService storjSno15mService;

    public StorjSno15mJob(StorjSno15mService storjSno15mService) {
        this.storjSno15mService = storjSno15mService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO 15m job...");
        storjSno15mService.runJob();
        log.info("Finished.");
    }
}
