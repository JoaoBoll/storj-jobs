package com.jvprojects.jobmaster.resources;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
@CrossOrigin(origins = "*")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    private final Scheduler scheduler;

    public JobController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/5s")
    public ResponseEntity<String> trigger5s() {
        return triggerJob("storjSnoCollectorJobTrigger");
    }

    @PostMapping("/15s")
    public ResponseEntity<String> trigger15s() {
        return triggerJob("storjSnoCollectorJobTrigger");
    }

    @PostMapping("/30s")
    public ResponseEntity<String> trigger30s() {
        return triggerJob("storjSnoCollectorJobTrigger");
    }

    @PostMapping("/5m")
    public ResponseEntity<String> trigger5m() {
        return triggerJob("storjSno5mJobTrigger");
    }

    @PostMapping("/15m")
    public ResponseEntity<String> trigger15m() {
        return triggerJob("storjSno15mJobTrigger");
    }

    @PostMapping("/30m")
    public ResponseEntity<String> trigger30m() {
        return triggerJob("storjSno30mJobTrigger");
    }

    @PostMapping("/1h")
    public ResponseEntity<String> trigger1h() {
        return triggerJob("storjSnoHourJobTrigger");
    }

    @PostMapping("/1d")
    public ResponseEntity<String> trigger1d() {
        return triggerJob("storjSnoDayJobTrigger");
    }

    @PostMapping("/1w")
    public ResponseEntity<String> trigger1w() {
        return triggerJob("storjSnoWeekJobTrigger");
    }

    @PostMapping("/1mo")
    public ResponseEntity<String> trigger1mo() {
        return triggerJob("storjSnoMonthJobTrigger");
    }

    private ResponseEntity<String> triggerJob(String triggerName) {
        try {
            scheduler.triggerJob(new org.quartz.JobKey(triggerName.replace("Trigger", "")), null);
            log.info("Manually triggered job: {}", triggerName);
            return ResponseEntity.ok("Job triggered successfully: " + triggerName);
        } catch (SchedulerException e) {
            log.error("Failed to trigger job: {}", triggerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to trigger job: " + e.getMessage());
        }
    }

}