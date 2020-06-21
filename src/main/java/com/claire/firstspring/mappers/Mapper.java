package com.claire.firstspring.mappers;

public interface Mapper<Type1, Type2> {

    Type1 toFirst(Type2 type2);
    Type2 toSecond(Type1 type1);
}
