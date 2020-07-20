package com.london.service.impl;

import com.london.client.HerokuAppClient;
import com.london.common.ApplicationException;
import com.london.configuration.GeoCodeConfiguration;
import com.london.dto.GeoCode;
import com.london.dto.People;
import com.london.service.SearchService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    private SearchService searchService;

    @Mock
    private HerokuAppClient herokuAppClient;

    @Mock
    private GeoCodeConfiguration geoCodeConfiguration;

    @BeforeEach
    void init(){
        searchService = new SearchServiceImpl(herokuAppClient, geoCodeConfiguration);
    }

    @Test
    void testNotNullResult() throws ApplicationException {
        assertNotNull(searchService.searchPeopleWithinRadius("London", 50D));
    }

    @Test
    void testReturnAllPeopleFromCity() throws ApplicationException {
        when(herokuAppClient.listPeopleInCity("London"))
            .thenReturn(Lists.newArrayList(People.builder().id(1).build(), People.builder().id(2).build()));

        List<People> results = searchService.searchPeopleWithinRadius("London", 50D);

        assertNotNull(results);
        assertEquals(2, results.size(), "Expected number of people are not returned");
        assertEquals(1, results.get(0).getId(), "Unexpected people with first name returned");
        assertEquals(2, results.get(1).getId(), "Unexpected people with first name returned");
    }

    @Test
    void testValidCityGeoCodeConfiguration() throws ApplicationException {
        when(herokuAppClient.listPeople())
            .thenReturn(Lists.newArrayList(People.builder().id(1).build(), People.builder().id(2).build()));

        Exception exception = assertThrows(ApplicationException.class, () -> searchService.searchPeopleWithinRadius("London", 50D));

        assertEquals("Application internal error", exception.getMessage());
    }

    @Test
    void testRadiusFilter() throws ApplicationException {
        when(geoCodeConfiguration.getCityMap())
            .thenReturn(Collections.singletonMap("London", GeoCode.builder().latitude(51.509865).longitude(-0.118092).build()));

        when(herokuAppClient.listPeople())
            .thenReturn(Lists.newArrayList(
                People.builder().id(1).latitude(51.509865).longitude(-0.118092).build(),
                People.builder().id(2).latitude(48.85756).longitude(2.34280).build()));


        List<People> results = searchService.searchPeopleWithinRadius("London", 50D);
        assertEquals(1, results.size(), "Error in radius filtering");
        assertEquals(1, results.get(0).getId(), "Filtered results invalid");
    }

    @Test
    void testRadiusFilterBoundaryConditions() throws ApplicationException {
        when(geoCodeConfiguration.getCityMap())
            .thenReturn(Collections.singletonMap("London", GeoCode.builder().latitude(51.509865).longitude(-0.118092).build()));

        when(herokuAppClient.listPeople())
            .thenReturn(Lists.newArrayList(
                People.builder().id(1).latitude(51.509865).longitude(-0.118092).build(),
                People.builder().id(2).latitude(51.999672).longitude(0.743579).build()));

        List<People> results = searchService.searchPeopleWithinRadius("London", 50D);
        assertEquals(2, results.size(), "Error in radius filtering");
        assertEquals(1, results.get(0).getId(), "Filtered results invalid");
        assertEquals(2, results.get(1).getId(), "Filtered results invalid");
    }

    @Test
    void testCityListAndRadiusListAreMerged() throws ApplicationException {
        when(herokuAppClient.listPeopleInCity("London"))
            .thenReturn(Lists.newArrayList(People.builder().id(1).build(), People.builder().id(2).build()));

        when(geoCodeConfiguration.getCityMap())
            .thenReturn(Collections.singletonMap("London", GeoCode.builder().latitude(51.509865).longitude(-0.118092).build()));

        when(herokuAppClient.listPeople())
            .thenReturn(Lists.newArrayList(
                People.builder().id(3).latitude(51.509865).longitude(-0.118092).build(),
                People.builder().id(4).latitude(51.999672).longitude(0.743579).build()));

        List<People> results = searchService.searchPeopleWithinRadius("London", 50D);
        assertEquals(4, results.size(), "Error in radius filtering");
        assertIterableEquals(Lists.newArrayList(
            People.builder().id(1).build(),
            People.builder().id(2).build(),
            People.builder().id(3).build(),
            People.builder().id(4).build()), results, "Filtered results invalid");
    }

    @Test
    void testSamePersonShouldNotReturnedFromCityListAndRadiusList() throws ApplicationException {
        when(herokuAppClient.listPeopleInCity("London"))
            .thenReturn(Lists.newArrayList(People.builder().id(1).build(), People.builder().id(2).build()));

        when(geoCodeConfiguration.getCityMap())
            .thenReturn(Collections.singletonMap("London", GeoCode.builder().latitude(51.509865).longitude(-0.118092).build()));

        when(herokuAppClient.listPeople())
            .thenReturn(Lists.newArrayList(
                People.builder().id(1).latitude(51.509865).longitude(-0.118092).build(),
                People.builder().id(2).latitude(51.999672).longitude(0.743579).build()));

        List<People> results = searchService.searchPeopleWithinRadius("London", 50D);
        assertEquals(2, results.size(), "Error in radius filtering");
        assertIterableEquals(Lists.newArrayList(
            People.builder().id(1).build(),
            People.builder().id(2).build()), results, "Filtered results invalid");
    }
}
