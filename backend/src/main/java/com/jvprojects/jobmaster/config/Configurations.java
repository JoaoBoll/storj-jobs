package com.jvprojects.jobmaster.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class Configurations {

    @Value("${storj.urls}")
    private String urlsRaw;

    public List<String> getUrls() {
        return Arrays.stream(urlsRaw.split(","))
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .collect(Collectors.toList());
    }

}
