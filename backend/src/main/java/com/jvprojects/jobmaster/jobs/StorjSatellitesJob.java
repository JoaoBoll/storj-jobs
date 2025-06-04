package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.dto.StorjSatellitesDto;
import com.jvprojects.jobmaster.repositories.StorjSnoRepository;
import com.jvprojects.jobmaster.services.StorjSatellitesService;
import com.jvprojects.jobmaster.services.StorjSnoService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DisallowConcurrentExecution
public class StorjSatellitesJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoService.class);

    private final StorjSatellitesService storjSatellitesService;
    private final StorjSnoRepository storjSnoRepository;

    public StorjSatellitesJob(StorjSatellitesService storjSatellitesService, StorjSnoRepository storjSnoRepository) {
        this.storjSatellitesService = storjSatellitesService;
        this.storjSnoRepository = storjSnoRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Searching for Storj Satellites...");

        List<StorjSatellitesDto> itens = storjSatellitesService.fetchStorjSatellites();

        log.info("Saving data...");

        storjSatellitesService.saveAll(itens);

        log.info("Finished.");
    }

}
