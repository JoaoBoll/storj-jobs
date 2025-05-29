package com.jvprojects.jobmaster.scheduler;

import com.jvprojects.jobmaster.jobs.StorjHourlyJob;
import com.jvprojects.jobmaster.jobs.StorjSatellitesJob;
import com.jvprojects.jobmaster.jobs.StorjSnoJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;

@Configuration
public class QuartzConfig {

    // Configuration for the StorjSno Job, which runs every second with an initial delay of 10 seconds.
    // This job will execute continuously and is stored durably.
    @Bean
    public JobDetail storjSnoJobDetail() {
        return JobBuilder.newJob(StorjSnoJob.class)
                .withIdentity("storjSnoJob")
                .storeDurably() // Ensures the job remains in Quartz even if no triggers are currently attached
                .build();
    }

    @Bean
    public Trigger storjSnoJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoJobTrigger")
                .forJob(storjSnoJobDetail())
                .startAt(Date.from(Instant.now().plusSeconds(10))) // Initial delay of 10 seconds
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1)) // Executes every second
                .build();
    }

    // Configuration for the StorjSatellites Job, which runs exactly at the start of each minute (00:00:00, 00:01:00, etc.)
    // Initial delay of 10 seconds before the first execution
    @Bean
    public Trigger storjSatellitesJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSatellitesJobTrigger")
                .forJob(storjSatellitesJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSatellitesJobDetail() {
        return JobBuilder.newJob(StorjSatellitesJob.class)
                .withIdentity("storjSatellitesJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjHourly Job, which runs precisely at the start of each hour (00:00, 01:00, 02:00, etc.)
    // The job executes according to UTC time zone to ensure global consistency.
    @Bean
    public Trigger storjHourlyJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjHourlyJobTrigger")
                .forJob(storjHourlyJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjHourlyJobDetail() {
        return JobBuilder.newJob(StorjHourlyJob.class)
                .withIdentity("storjHourlyJob")
                .storeDurably()
                .build();
    }

}
