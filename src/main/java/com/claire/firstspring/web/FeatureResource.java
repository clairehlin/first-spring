package com.claire.firstspring.web;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.service.FeatureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/features")
public class FeatureResource {

    private final FeatureService featureService;

    public FeatureResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public Set<Feature> features() {
        return featureService.list();
    }

    @PostMapping("/{name}")
    public void create(@PathVariable String name) {
        featureService.create(new Feature(name));
    }
}