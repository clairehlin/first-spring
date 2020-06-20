package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;

import java.util.Set;

public interface FeatureRepository extends MainTableAwareRepository {
    Set<Feature> itemFeatures(Integer id);

    Integer id(Feature feature);

    Set<Feature> list();

    void create(Feature name);
}
