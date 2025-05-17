package com.jvprojects.jobmaster.scheduler;

import com.jvprojects.jobmaster.jobs.StorjSatellitesJob;
import com.jvprojects.jobmaster.jobs.StorjSnoJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;

@Configuration
public class QuartzConfig {

    /* STORJ SNO JOB (a cada 1s, com delay inicial de 10s) */
    @Bean
    public JobDetail storjSnoJobDetail() {
        return JobBuilder.newJob(StorjSnoJob.class)
                .withIdentity("storjSnoJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger storjSnoJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoJobTrigger")
                .forJob(storjSnoJobDetail())
                .startAt(Date.from(Instant.now().plusSeconds(10))) // Delay de 10s
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1))
                .build();
    }

    /* STORJ SATELLITES JOB (a cada 1s, com delay inicial de 10s) */
    @Bean
    public JobDetail storjSatellitesJobDetail() {
        return JobBuilder.newJob(StorjSatellitesJob.class)
                .withIdentity("storjSatellitesJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger storjSatellitesJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSatellitesJobTrigger")
                .forJob(storjSatellitesJobDetail())
                .startAt(Date.from(Instant.now().plusSeconds(10)))
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5))
                .build();
    }
}
