package com.london.ws.controller;

import com.london.common.ApplicationException;
import com.london.dto.People;
import com.london.service.SearchService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(PeopleController.class)
class PeopleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    void testDefaultResponse() throws Exception {
        mockMvc.perform(get("/people"))
            .andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_VALUE));
    }

    @Test
    void testParameterValidation() throws Exception {
        mockMvc.perform(get("/people").params(new LinkedMultiValueMap<String, String>() {{
            add("city", "London");
            add("miles", "-1");
        }}))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("listPeople.miles: The value must be positive number"));

        mockMvc.perform(get("/people").params(new LinkedMultiValueMap<String, String>() {{
            add("city", "London");
            add("miles", "notDouble");
        }}))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("NumberFormatException: For input string: \"notDouble\"")));
    }

    @Test
    void testApplicationException() throws Exception {
        when(searchService.searchPeopleWithinRadius(anyString(),anyDouble())).thenThrow(new ApplicationException(null, "Application exception"));

        mockMvc.perform(get("/people"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Application exception"));
    }

    @Test
    void testDefaultParameterResponse() throws Exception {
        when(searchService.searchPeopleWithinRadius(PeopleController.DEFAULT_CITY, Double.parseDouble(PeopleController.DEFAULT_MILES)))
            .thenReturn(new ArrayList<People>(){
            {
                add(People.builder().firstName("John").build());
            }
        });
        mockMvc.perform(get("/people"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$[0].first_name", Matchers.is("John")));
    }

    @Test
    void testSearchResponse() throws Exception {
        String city = "Paris";
        Double radius = 25.5;

        when(searchService.searchPeopleWithinRadius(city, radius))
            .thenReturn(new ArrayList<People>(){
                {
                    add(People.builder().id(1).firstName("John").lastName("Wood")
                        .email("jw@gmail.com").ipAddress("122.4.32.22")
                        .latitude(51.509865).longitude(-0.118092).build());
                    add(People.builder().id(2).firstName("Mechelle").lastName("Boam").build());
                }
            });
        mockMvc.perform(get("/people").params(new LinkedMultiValueMap<String, String>() {{
            add("city", city);
            add("miles", radius.toString());
        }}))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$[0].id", Matchers.is(1)))
            .andExpect(jsonPath("$[0].first_name", Matchers.is("John")))
            .andExpect(jsonPath("$[0].last_name", Matchers.is("Wood")))
            .andExpect(jsonPath("$[0].email", Matchers.is("jw@gmail.com")))
            .andExpect(jsonPath("$[0].ip_address", Matchers.is("122.4.32.22")))
            .andExpect(jsonPath("$[0].latitude", Matchers.is(51.509865)))
            .andExpect(jsonPath("$[0].longitude", Matchers.is(-0.118092)))
            .andExpect(jsonPath("$[1].first_name", Matchers.is("Mechelle")))
            .andExpect(jsonPath("$[1].last_name", Matchers.is("Boam")));
    }


}
