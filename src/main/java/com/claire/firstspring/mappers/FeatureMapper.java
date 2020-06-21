package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Feature;
import org.springframework.stereotype.Component;

@Component
public class FeatureMapper implements Mapper<Feature, String> {

    @Override
    public Feature toFirst(String featureName) {
        return new Feature(featureName);
    }

    @Override
    public String toSecond(Feature feature) {
        return feature.name();
    }
}
