package com.claire.firstspring.web.model;

public class FeatureUpdate {
    public String currentName;
    public String newName;

    public static FeatureUpdate featureUpdate(String currentName, String newName) {
        FeatureUpdate featureUpdate = new FeatureUpdate();
        featureUpdate.currentName = currentName;
        featureUpdate.newName = newName;
        return featureUpdate;
    }
}
