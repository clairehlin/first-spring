package com.claire.firstspring.service;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.repository.FeatureRepository;
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
        featureRepository.create(feature);
    }
}
