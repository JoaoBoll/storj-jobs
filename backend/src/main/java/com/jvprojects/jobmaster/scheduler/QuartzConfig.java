package com.jvprojects.jobmaster.scheduler;

import com.jvprojects.jobmaster.jobs.sno.StorjSno15mJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSno30mJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSno5mJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoCollectorJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoDayJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoHourJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoMinuteJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoMonthJob;
import com.jvprojects.jobmaster.jobs.sno.StorjSnoWeekJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Only the collector runs on a cron schedule: it fetches the Storj node API every 5 seconds
 * and cascades every aggregation level (minute, 5m, 15m, 30m, hour, day, week, month) whose
 * time boundary was just reached, in the same run. The aggregation jobs below are kept as
 * durable, trigger-less job details purely so they stay reachable for a manual/forced run
 * from JobController; they are never scheduled independently.
 */
@Configuration
public class QuartzConfig {

    @Bean
    public Trigger storjSnoCollectorJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoCollectorJobTrigger")
                .forJob(storjSnoCollectorJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0,5,10,15,20,25,30,35,40,45,50,55 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSnoCollectorJobDetail() {
        return JobBuilder.newJob(StorjSnoCollectorJob.class)
                .withIdentity("storjSnoCollectorJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSnoMinuteJobDetail() {
        return JobBuilder.newJob(StorjSnoMinuteJob.class)
                .withIdentity("storjSnoMinuteJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSno5mJobDetail() {
        return JobBuilder.newJob(StorjSno5mJob.class)
                .withIdentity("storjSno5mJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSno15mJobDetail() {
        return JobBuilder.newJob(StorjSno15mJob.class)
                .withIdentity("storjSno15mJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSno30mJobDetail() {
        return JobBuilder.newJob(StorjSno30mJob.class)
                .withIdentity("storjSno30mJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSnoHourJobDetail() {
        return JobBuilder.newJob(StorjSnoHourJob.class)
                .withIdentity("storjSnoHourJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSnoDayJobDetail() {
        return JobBuilder.newJob(StorjSnoDayJob.class)
                .withIdentity("storjSnoDayJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSnoWeekJobDetail() {
        return JobBuilder.newJob(StorjSnoWeekJob.class)
                .withIdentity("storjSnoWeekJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail storjSnoMonthJobDetail() {
        return JobBuilder.newJob(StorjSnoMonthJob.class)
                .withIdentity("storjSnoMonthJob")
                .storeDurably()
                .build();
    }

}
