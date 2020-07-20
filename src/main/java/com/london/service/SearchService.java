package com.london.service;

import com.london.common.ApplicationException;
import com.london.dto.People;

import java.util.List;

public interface SearchService {
    /**
     * Search people listed in {@code city} or coordinates are within {@code radius} in miles
     * @param city city name
     * @param radius in miles
     * @return list of People
     */
    List<People> searchPeopleWithinRadius(String city, Double radius) throws ApplicationException;
}
