package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.services.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    private final StorjSnoSecondService storjSnoSecondService;
    private final StorjSnoMinuteService storjSnoMinuteService;
    private final StorjSnoHourService storjSnoHourService;
    private final StorjSnoDayService storjSnoDayService;
    private final StorjSnoWeekService storjSnoWeekService;
    private final StorjSnoMonthService storjSnoMonthService;

    public JobController(StorjSnoSecondService storjSnoSecondService, StorjSnoMinuteService storjSnoMinuteService, StorjSnoHourService storjSnoHourService, StorjSnoDayService storjSnoDayService, StorjSnoWeekService storjSnoWeekService, StorjSnoMonthService storjSnoMonthService) {
        this.storjSnoSecondService = storjSnoSecondService;
        this.storjSnoMinuteService = storjSnoMinuteService;
        this.storjSnoHourService = storjSnoHourService;
        this.storjSnoDayService = storjSnoDayService;
        this.storjSnoWeekService = storjSnoWeekService;
        this.storjSnoMonthService = storjSnoMonthService;
    }

    @GetMapping("/snoSeconds")
    public String storjSnoSecondsService() {
        storjSnoSecondService.runJob();
        return "Job executado!";
    }
    @GetMapping("/snoMinute")
    public String storjSnoHourService() {
        storjSnoMinuteService.runJob();
        return "Job executado!";
    }
    @GetMapping("/snoHour")
    public String snoHour() {
        storjSnoHourService.runJob();
        return "Job executado!";
    }
    @GetMapping("/snoDay")
    public String snoDay() {
        storjSnoDayService.runJob();
        return "Job executado!";
    }
    @GetMapping("/snoWeek")
    public String snoWeek() {
        storjSnoWeekService.runJob();
        return "Job executado!";
    }
    @GetMapping("/snoMonth")
    public String snoMonth() {
        storjSnoMonthService.runJob();
        return "Job executado!";
    }
}