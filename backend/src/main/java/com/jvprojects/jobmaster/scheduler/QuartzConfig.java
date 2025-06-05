package com.jvprojects.jobmaster.scheduler;

import com.jvprojects.jobmaster.jobs.*;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // Configuration for the StorjSno Job, which runs every 5 second with an initial delay of 10 seconds.
    // This job will execute continuously and is stored durably.
    @Bean
    public Trigger storjSnoJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoJobTrigger")
                .forJob(storjSnoJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0,5,10,15,20,25,30,35,40,45,50,55 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSnoJobDetail() {
        return JobBuilder.newJob(StorjSnoSecondJob.class)
                .withIdentity("storjSnoJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjSnoMinuteJob Job, which runs exactly at the start of each minute (00:00:00, 00:01:00, etc.)
    @Bean
    public Trigger storjSnoMinuteJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoMinuteJobTrigger")
                .forJob(storjSnoMinuteJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSnoMinuteJobDetail() {
        return JobBuilder.newJob(StorjSnoMinuteJob.class)
                .withIdentity("storjSnoMinuteJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjSnoHourJob Job, which runs exactly at the start of each hour (00:00:00, 00:01:00, etc.)
    @Bean
    public Trigger storjSnoHourJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoHourJobTrigger")
                .forJob(storjSnoHourJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSnoHourJobDetail() {
        return JobBuilder.newJob(StorjSnoHourJob.class)
                .withIdentity("storjSnoHourJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjSnoDayJob Job, which runs exactly at the start of each hour (00:00:00, 00:01:00, etc.)
    @Bean
    public Trigger storjSnoDayJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoDayJobTrigger")
                .forJob(storjSnoDayJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    @Bean
    public JobDetail storjSnoDayJobDetail() {
        return JobBuilder.newJob(StorjSnoDayJob.class)
                .withIdentity("storjSnoDayJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjSnoWeekJob Job, which runs exactly at the start of each week
    @Bean
    public Trigger storjSnoWeekJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoWeekJobTrigger")
                .forJob(storjSnoWeekJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * SUN")) // Executa todo domingo à meia-noite
                .build();
    }

    @Bean
    public JobDetail storjSnoWeekJobDetail() {
        return JobBuilder.newJob(StorjSnoWeekJob.class)
                .withIdentity("storjSnoWeekJob")
                .storeDurably()
                .build();
    }

    // Configuration for the StorjSnoMonthJob Job, which runs exactly at the start of each month
    @Bean
    public Trigger storjSnoMonthJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("storjSnoMonthJobTrigger")
                .forJob(storjSnoMonthJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * SUN")) // Executa todo domingo à meia-noite
                .build();
    }

    @Bean
    public JobDetail storjSnoMonthJobDetail() {
        return JobBuilder.newJob(StorjSnoMonthJob.class)
                .withIdentity("storjSnoMonthJob")
                .storeDurably()
                .build();
    }
//    // Configuration for the StorjSnoMinuteJob Job, which runs exactly at the start of each hour (00:00:00, 00:01:00, etc.)
//    @Bean
//    public Trigger storjSnoHourJobTrigger() {
//        return TriggerBuilder.newTrigger()
//                .withIdentity("storjSnoHourJobTrigger")
//                .forJob(storjSnoHourJobDetail())
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
//                .build();
//    }
//
//    @Bean
//    public JobDetail storjSnoHourJobDetail() {
//        return JobBuilder.newJob(StorjSnoHourJob.class)
//                .withIdentity("storjSnoHourJob")
//                .storeDurably()
//                .build();
//    }
    // Configuration for the StorjSnoMinuteJob Job, which runs exactly at the start of each hour (00:00:00, 00:01:00, etc.)
//    @Bean
//    public Trigger storjSnoHourJobTrigger() {
//        return TriggerBuilder.newTrigger()
//                .withIdentity("storjSnoHourJobTrigger")
//                .forJob(storjSnoHourJobDetail())
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
//                .build();
//    }
//
//    @Bean
//    public JobDetail storjSnoHourJobDetail() {
//        return JobBuilder.newJob(StorjSnoHourJob.class)
//                .withIdentity("storjSnoHourJob")
//                .storeDurably()
//                .build();
//    }

    // Configuration for the StorjSatellites Job, which runs exactly at the start of each minute (00:00:00, 00:01:00, etc.)
    // Initial delay of 10 seconds before the first execution
//    @Bean
//    public Trigger storjSatellitesJobTrigger() {
//        return TriggerBuilder.newTrigger()
//                .withIdentity("storjSatellitesJobTrigger")
//                .forJob(storjSatellitesJobDetail())
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
//                .build();
//    }
//
//    @Bean
//    public JobDetail storjSatellitesJobDetail() {
//        return JobBuilder.newJob(StorjSatellitesJob.class)
//                .withIdentity("storjSatellitesJob")
//                .storeDurably()
//                .build();
//    }


    // Configuration for the StorjHourly Job, which runs precisely at the start of each hour (00:00, 01:00, 02:00, etc.)
    // The job executes according to UTC time zone to ensure global consistency.
//    @Bean
//    public Trigger storjHourlyJobTrigger() {
//        return TriggerBuilder.newTrigger()
//                .withIdentity("storjHourlyJobTrigger")
//                .forJob(storjHourlyJobDetail())
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
//                .build();
//    }
//
//    @Bean
//    public JobDetail storjHourlyJobDetail() {
//        return JobBuilder.newJob(StorjHourlyJob.class)
//                .withIdentity("storjHourlyJob")
//                .storeDurably()
//                .build();
//    }

}
