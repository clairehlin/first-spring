package com.claire.firstspring.service;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.repository.FeatureRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SimpleFeatureService implements FeatureService {

    private final FeatureRepository featureRepository;

    public SimpleFeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    public Set<Feature> list() {
        return featureRepository.list();
    }

    @Override
    public void create(Feature feature) {
        Validate.notNull(feature);
        featureRepository.create(feature);
    }

    @Override
    public void delete(String featureName) {
        Validate.notBlank(featureName, "feature name cannot be blank.");
        featureRepository.delete(featureName);
    }

    @Override
    public void updateFeature(String currentName, String newName) {
        Validate.notBlank(currentName, "current feature name cannot be blank.");
        Validate.notBlank(newName, "new feature name cannot be blank.");
        featureRepository.update(currentName, newName);
    }
}
