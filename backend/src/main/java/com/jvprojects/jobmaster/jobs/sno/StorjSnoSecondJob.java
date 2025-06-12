package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSnoSecondService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoSecondJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoSecondService.class);

    private final StorjSnoSecondService storjSnoSecondService;

    public StorjSnoSecondJob(StorjSnoSecondService storjSnoSecondService) {
        this.storjSnoSecondService = storjSnoSecondService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Second job...");

        storjSnoSecondService.runJob();

        log.info("Finished.");
    }
}