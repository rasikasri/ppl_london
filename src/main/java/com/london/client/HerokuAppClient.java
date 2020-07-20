package com.london.client;

import com.london.dto.People;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "heroku-app", url = "${herokuApp.url:}", fallback = HystrixFallback.class)
public interface HerokuAppClient {

    /**
     * Gets people by city.
     *
     * @param city the city name
     * @return the list of people living in the {@code city}
     */
    @RequestMapping(
        value = "/city/{city}/users",
        method = GET,
        produces = APPLICATION_JSON_VALUE)
    List<People> listPeopleInCity(@PathVariable("city") String city);

    /**
     * Get {@code List} of people.
     *
     * @return the list of people
     */
    @RequestMapping(
        value = "/users",
        method = GET,
        produces = APPLICATION_JSON_VALUE)
    List<People> listPeople();
}

