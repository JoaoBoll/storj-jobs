package com.jvprojects.jobmaster.jobs.sno;

import com.jvprojects.jobmaster.services.sno.StorjSnoMinuteService;
import com.jvprojects.jobmaster.services.sno.StorjSnoMonthService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class StorjSnoMonthJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoMinuteService.class);

    private final StorjSnoMonthService storjSnoMonthService;

    public StorjSnoMonthJob(StorjSnoMonthService storjSnoMonthService) {
        this.storjSnoMonthService = storjSnoMonthService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running Storj SNO Month job...");

        storjSnoMonthService.runJob();

        log.info("Finished.");
    }
}