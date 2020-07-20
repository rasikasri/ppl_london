package com.london.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.http.RequestMethod.ANY;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class PeopleSearchIT extends IntegrationBase{

    @Test
    public void testSearchPeople(){
        //Given
        mockResponse("/city/London/users", getResponseResources("city_london.json"), OK.getStatusCode(), ANY);
        mockResponse("/users", getResponseResources("all_people.json"), OK.getStatusCode(), ANY);

        //When
        ResponseEntity<String> response = sendRequestToServer("/people");

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        assertEquals(APPLICATION_JSON_VALUE, response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));
        assertEquals(9, (Integer) JsonPath.parse(response.getBody()).read("$.length()"));
    }

}
