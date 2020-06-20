package com.claire.firstspring.service;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Restaurant;

import java.util.List;
import java.util.Set;

public interface FeatureService {
    Set<Feature> list();

    void create(Feature feature);
}
