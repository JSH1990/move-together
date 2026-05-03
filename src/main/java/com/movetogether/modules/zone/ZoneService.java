package com.movetogether.modules.zone;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() > 0) return;

        Resource resource = new ClassPathResource("zones_kr.csv");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            List<Zone> zoneList = reader.lines()
                    .map(line -> line.split(","))
                    .filter(arr -> arr.length >= 3)
                    .map(arr -> Zone.builder()
                            .city(arr[0])
                            .localNameOfCity(arr[1])
                            .province(arr[2])
                            .build())
                    .toList();

            zoneRepository.saveAll(zoneList);
        }
    }
}
