package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;

import java.util.Optional;
import java.util.Set;

public interface FeatureRepository extends MainTableAwareRepository {
    Set<Feature> itemFeatures(Integer id);

    Optional<Integer> id(Feature feature);

    Set<Feature> list();

    void create(Feature name);

    void delete(String featureName);

    void update(String currentName, String newName);
}
