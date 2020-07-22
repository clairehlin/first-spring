package com.claire.firstspring.service;

import com.claire.firstspring.model.Feature;

import java.util.Set;

public interface FeatureService {
    Set<Feature> list();

    void create(Feature feature);

    void delete(String featureName);

    void updateFeature(String currentName, String newName);
}
