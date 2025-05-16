package com.jvprojects.jobmaster.scheduler;

import com.jvprojects.jobmaster.jobs.storj.StorjSnoJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    /* STORJ SNO JOB per second */
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
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1))
                .build();
    }
}