package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSnoDayService;
import com.jvprojects.jobmaster.services.sno.StorjSnoMinuteService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoDayJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoDayService storjSnoDayService;

    public StorjSnoDayJob(StorjSnoDayService storjSnoDayService) {
        this.storjSnoDayService = storjSnoDayService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Day job...");

        storjSnoDayService.runJob();

        log.info("Finished.");
    }
}