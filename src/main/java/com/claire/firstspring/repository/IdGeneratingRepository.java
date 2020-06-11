package com.claire.firstspring.repository;

public interface IdGeneratingRepository {
    int nextId(MainTableAwareRepository mainTableAwareRepository);
}