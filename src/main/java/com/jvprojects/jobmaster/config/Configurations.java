package com.jvprojects.jobmaster.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
public class Configurations {

    @Value("${storj.urls}")
    private String urlsRaw;

    public List<String> getUrls() {
        return Arrays.asList(urlsRaw.split(","));
    }

}
