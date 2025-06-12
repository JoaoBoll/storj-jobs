package com.jvprojects.jobmaster.jobs;

import com.jvprojects.jobmaster.dto.StorjSatellitesDto;
import com.jvprojects.jobmaster.repositories.sno.StorjSnoSecondRepository;
import com.jvprojects.jobmaster.services.StorjSatellitesService;
import com.jvprojects.jobmaster.services.sno.StorjSnoSecondService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DisallowConcurrentExecution
public class StorjSatellitesJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StorjSnoSecondService.class);

    private final StorjSatellitesService storjSatellitesService;
    private final StorjSnoSecondRepository storjSnoSecondRepository;

    public StorjSatellitesJob(StorjSatellitesService storjSatellitesService, StorjSnoSecondRepository storjSnoSecondRepository) {
        this.storjSatellitesService = storjSatellitesService;
        this.storjSnoSecondRepository = storjSnoSecondRepository;
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
