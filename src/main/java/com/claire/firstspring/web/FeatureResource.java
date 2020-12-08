package com.claire.firstspring.web;

import com.claire.firstspring.mappers.FeatureMapper;
import com.claire.firstspring.model.Feature;
import com.claire.firstspring.service.FeatureService;
import com.claire.firstspring.web.model.FeatureUpdate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/features")
@Transactional
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

    @PutMapping("/{current-name}/name/{new-name}")
    public void updateFeature(@PathVariable("current-name") String currentName, @PathVariable("new-name") String newName) {
        featureService.updateFeature(currentName, newName);
    }

    @PutMapping
    public void updateFeatures(@RequestBody List<FeatureUpdate> featureUpdates) {
        featureUpdates.forEach(featureUpdate -> this.updateFeature(featureUpdate.currentName, featureUpdate.newName));
    }

    @PutMapping("/{feature-name}")
    public void createFeature(@PathVariable("feature-name") String featureName) {
        Feature feature = new Feature(featureName);
        featureService.create(feature);
    }

    @DeleteMapping("/{feature-name}")
    public void deleteFeature(@PathVariable("feature-name") String featureName) {
        featureService.delete(featureName);
    }
}