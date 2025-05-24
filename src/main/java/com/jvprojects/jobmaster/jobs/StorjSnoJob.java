package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.dto.StorjSnoDto;
import com.jvprojects.jobmaster.repositories.StorjSnoRepository;
import com.jvprojects.jobmaster.services.StorjSnoService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DisallowConcurrentExecution
public class StorjSnoJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final StorjSnoService storjSnoService;
    private final StorjSnoRepository storjSnoRepository;

    public StorjSnoJob(StorjSnoService storjSnoService, StorjSnoRepository storjSnoRepository) {
        this.storjSnoService = storjSnoService;
        this.storjSnoRepository = storjSnoRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Searching for Storj Node Operators (SNO)...");

        List<StorjSnoDto> itens = storjSnoService.fetchStorjNodes();

        log.info("Saving data...");

        storjSnoService.saveAll(itens);

        log.info("Finished.");
    }
}