package com.jvprojects.jobmaster;

import com.jvprojects.jobmaster.services.StorjNodeService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner {

    private final StorjNodeService storjNodeService;

    public StartupRunner(StorjNodeService storjNodeService) {
        this.storjNodeService = storjNodeService;
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void runAfterStartup() {
        System.out.println("🚀 Aplicação totalmente iniciada. Executando serviço...");
        storjNodeService.executeOnStart();
    }
}

