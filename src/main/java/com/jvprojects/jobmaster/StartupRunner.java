package com.jvprojects.jobmaster;

import com.jvprojects.jobmaster.repositories.StorjNodeRepository;
import com.jvprojects.jobmaster.services.StorjNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final StorjNodeService storjNodeService;
    private final StorjNodeRepository storjNodeRepository;

    @Autowired
    public StartupRunner(StorjNodeService storjNodeService, StorjNodeRepository storjNodeRepository) {
        this.storjNodeService = storjNodeService;
        this.storjNodeRepository = storjNodeRepository;
    }
    
    @Override
    public void run(String... args) {

        storjNodeService.executeOnStart();
        
    }
}

