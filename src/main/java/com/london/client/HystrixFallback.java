package com.london.client;

import com.london.dto.People;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class HystrixFallback implements HerokuAppClient {

    /**
     * Gets people by city.
     *
     * @param city the city name
     * @return the list of people living in the {@code city}
     */
    public List<People> listPeopleInCity(String city){
        return new ArrayList<>();
    }

    /**
     * Get {@code List} of people.
     *
     * @return the list of people
     */
    public List<People> listPeople(){
        return new ArrayList<>();
    }

}
