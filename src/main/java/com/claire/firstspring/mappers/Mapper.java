package com.claire.firstspring.mappers;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public interface Mapper<Type1, Type2> {

    Type1 toFirst(Type2 type2);

    Type2 toSecond(Type1 type1);

    default List<Type1> toFirsts(List<Type2> type2s) {
        return type2s.stream()
            .map(this::toFirst)
            .collect(toList());
    }

    default List<Type2> toSeconds(List<Type1> type1s) {
        return type1s.stream()
            .map(this::toSecond)
            .collect(toList());
    }

    default Set<Type1> toFirsts(Set<Type2> type2s) {
        return type2s.stream()
            .map(this::toFirst)
            .collect(toSet());
    }

    default Set<Type2> toSeconds(Set<Type1> type1s) {
        return type1s.stream()
            .map(this::toSecond)
            .collect(toSet());
    }
}
