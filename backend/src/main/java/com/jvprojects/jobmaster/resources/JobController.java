package com.jvprojects.jobmaster.resources;

import com.jvprojects.jobmaster.dto.StorjNodeResponse;
import com.jvprojects.jobmaster.entities.StorjNode;
import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.services.sno.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job")
@CrossOrigin(origins = "*")
public class JobController {

    private final StorjSnoSecondService storjSnoSecondService;
    private final StorjSnoMinuteService storjSnoMinuteService;
    private final StorjSnoHourService storjSnoHourService;
    private final StorjSnoDayService storjSnoDayService;
    private final StorjSnoWeekService storjSnoWeekService;
    private final StorjSnoMonthService storjSnoMonthService;
    private final StorjNodeRepository storjNodeRepository;

    public JobController(StorjSnoSecondService storjSnoSecondService, StorjSnoMinuteService storjSnoMinuteService, StorjSnoHourService storjSnoHourService, StorjSnoDayService storjSnoDayService, StorjSnoWeekService storjSnoWeekService, StorjSnoMonthService storjSnoMonthService, StorjNodeRepository storjNodeRepository) {
        this.storjSnoSecondService = storjSnoSecondService;
        this.storjSnoMinuteService = storjSnoMinuteService;
        this.storjSnoHourService = storjSnoHourService;
        this.storjSnoDayService = storjSnoDayService;
        this.storjSnoWeekService = storjSnoWeekService;
        this.storjSnoMonthService = storjSnoMonthService;
        this.storjNodeRepository = storjNodeRepository;
    }

    @GetMapping("/nodes")
    public List<StorjNodeResponse> nodes() {
        return storjNodeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private StorjNodeResponse toResponse(StorjNode node) {
        return new StorjNodeResponse(
                node.getId(),
                node.getNodeId(),
                node.getUrl(),
                node.getEnabled(),
                node.getAvailableDiskSpace(),
                node.getCreatedAt(),
                node.getUpdatedAt()
        );
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