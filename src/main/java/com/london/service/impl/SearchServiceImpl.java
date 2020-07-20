package com.london.service.impl;

import com.london.client.HerokuAppClient;
import com.london.common.ApplicationException;
import com.london.configuration.GeoCodeConfiguration;
import com.london.dto.GeoCode;
import com.london.dto.People;
import com.london.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final int EARTH_RADIUS = 3956; //in miles

    private final HerokuAppClient herokuAppClient;

    private final GeoCodeConfiguration geoCodeConfiguration;

    @Override
    public List<People> searchPeopleWithinRadius(String city, Double radius) throws ApplicationException {
        List<People> cityList = herokuAppClient.listPeopleInCity(city);
        List<People> fullList = herokuAppClient.listPeople();
        log.info("Number of people in city of {} are {}", city, cityList != null ? cityList.size() : 0);
        log.info("Total number of people are {}", fullList != null ? fullList.size() : 0);

        if (fullList != null && !fullList.isEmpty()) {
            fullList = radiusFilter(fullList, city, radius);
            log.info("Number of people in within {} miles of {} are {}", radius, city, fullList != null ? fullList.size() : 0);
        }

        //Merge both list and remove duplicates
        return Stream.of(cityList, fullList)
            .flatMap(x -> x == null ? null : x.stream())
            .distinct()
            .collect(Collectors.toList());
    }

    private List<People> radiusFilter(List<People> fullList, String city, Double radius) throws ApplicationException {
        GeoCode cityGeoCode = geoCodeConfiguration.getCityMap().get(city);
        if (cityGeoCode == null) {
            log.error("Could not find geo coordinate for city : {}", city);
            throw new ApplicationException(null, "Application internal error");
        }
        return fullList.stream().filter(isWithinRadius(cityGeoCode, radius)).collect(Collectors.toList());
    }

    private Predicate<People> isWithinRadius(GeoCode cityGeoCode, Double radius) {
        return i -> distance(i.getLatitude(), i.getLongitude(), cityGeoCode.getLatitude(), cityGeoCode.getLongitude()) <= radius;
    }

    private static double distance(double pplLat, double pplLon, double cityLat, double cityLon) {
        if ((pplLat == cityLat) && (pplLon == cityLon)) {
            return 0;
        } else {
            // Haversine formula
            double deltaLon = Math.toRadians(cityLon - pplLon);
            double deltaLat = Math.toRadians(cityLat - pplLat);
            double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(Math.toRadians(pplLat)) * Math.cos(Math.toRadians(cityLat)) * Math.pow(Math.sin(deltaLon / 2),2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            return(c * EARTH_RADIUS);
        }
    }
}
