package com.claire.firstspring.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return Objects.equals(name, feature.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Feature{" +
            "name='" + name + '\'' +
            '}';
    }
}
