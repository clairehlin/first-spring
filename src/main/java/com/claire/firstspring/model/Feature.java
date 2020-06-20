package com.claire.firstspring.model;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Feature {

    private final String name;

    public Feature(String name) {
        checkArgument(isNotBlank(name), "feature name cannot be blank but was [%s]", name);
        this.name = name;
    }

    public String name() {
        return name;
    }
}
