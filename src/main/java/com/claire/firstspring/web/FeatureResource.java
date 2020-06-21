package com.claire.firstspring.web;

import com.claire.firstspring.mappers.FeatureMapper;
import com.claire.firstspring.service.FeatureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/features")
public class FeatureResource {

    private final FeatureService featureService;
    private final FeatureMapper featureMapper;

    public FeatureResource(FeatureService featureService, FeatureMapper featureMapper) {
        this.featureService = featureService;
        this.featureMapper = featureMapper;
    }

    @GetMapping
    public Set<String> features() {
        return featureService.list()
            .stream()
            .map(featureMapper::toSecond)
            .collect(toSet());
    }

    @PostMapping("/{name}")
    public void create(@PathVariable String name) {
        featureService.create(featureMapper.toFirst(name));
    }
}